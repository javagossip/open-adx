package top.openadexchange.openapi.ssp.infra.index;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.roaringbitmap.IntConsumer;
import org.roaringbitmap.RoaringBitmap;
import org.springframework.util.Assert;

import com.chaincoretech.epc.annotation.Extension;
import com.google.common.hash.Hashing;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.commons.StreamUtils;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogSamplingConfig;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogType;
import top.openadexchange.openapi.ssp.application.factory.IndexKeysBuilder;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;

@Extension(keys = {"roaringBitmap", "default"})
@Slf4j
public class RoaringIndexService implements IndexService {

    // 广告位ID到DSP ID列表的索引
    private final Map<String, RoaringBitmap> adPlacementToDspIndex = new ConcurrentHashMap<>();
    //广告定向信息到 DSP ID列表的索引
    // 操作系统->DSP ID索引列表
    private final Map<String, RoaringBitmap> osIndex = new ConcurrentHashMap<>();
    // 设备类型->DSP ID索引列表
    private final Map<String, RoaringBitmap> deviceTypeIndex = new ConcurrentHashMap<>();
    //区域定向->DSP ID索引列表
    private final Map<String, RoaringBitmap> regionIndex = new ConcurrentHashMap<>();

    //广告位ID到日志采样配置的索引
    private final ConcurrentMap<Integer, RoaringBitmap> lscIndex = new ConcurrentHashMap<>();

    @Resource
    private IndexKeysBuilder indexKeysBuilder;

    public RoaringIndexService() {
    }

    @Override
    public void indexDsp(DspAggregate dspAggregate) {
        IndexKeys indexKeys = indexKeysBuilder.buildIndexKeys(dspAggregate);
        if (indexKeys == null) {
            log.error("DSP:{}索引信息为空", dspAggregate.getDsp().getName());
            return;
        }
        log.info("DSP:{}索引信息:{}", dspAggregate.getDsp().getName(), indexKeys);

        List<String> tagIdKeys = indexKeys.getTagIdKeys();
        List<String> osKeys = indexKeys.getOsKeys();
        List<String> deviceTypeKeys = indexKeys.getDeviceTypeKeys();
        List<String> regionKeys = indexKeys.getRegionKeys();

        Dsp dsp = dspAggregate.getDsp();
        addDspToIndex(tagIdKeys, adPlacementToDspIndex, dsp);
        addDspToIndex(osKeys, osIndex, dsp);
        addDspToIndex(deviceTypeKeys, deviceTypeIndex, dsp);
        addDspToIndex(regionKeys, regionIndex, dsp);
    }

    private void addDspToIndex(List<String> keys, Map<String, RoaringBitmap> index, Dsp dsp) {
        if (keys != null) {
            keys.forEach(key -> index.computeIfAbsent(key, k -> new RoaringBitmap()).add(dsp.getId()));
        }
    }

    @Override
    public void indexAdGroup(DspAggregate dspAggregate) {
        //TODO 暂不处理
    }

    @Override
    public List<Integer> searchDsps(IndexKeys indexKeys) {
        List<String> tagIdKeys = indexKeys.getTagIdKeys();
        List<String> osKeys = indexKeys.getOsKeys();
        List<String> deviceTypeKeys = indexKeys.getDeviceTypeKeys();
        List<String> regionKeys = indexKeys.getRegionKeys();

        RoaringBitmap adPlacementBitmap = mergeBitmaps(tagIdKeys, adPlacementToDspIndex);
        if (adPlacementBitmap.isEmpty()) {
            return Collections.emptyList();
        }
        RoaringBitmap osBitmap = mergeBitmaps(osKeys, osIndex);
        if (osBitmap.isEmpty()) {
            return Collections.emptyList();
        }
        RoaringBitmap deviceTypeBitmap = mergeBitmaps(deviceTypeKeys, deviceTypeIndex);
        if (deviceTypeBitmap.isEmpty()) {
            return Collections.emptyList();
        }
        RoaringBitmap regionBitmap = mergeBitmaps(regionKeys, regionIndex);
        if (regionBitmap.isEmpty()) {
            return Collections.emptyList();
        }
        List<RoaringBitmap> must =
                new ArrayList<>(List.of(adPlacementBitmap, osBitmap, deviceTypeBitmap, regionBitmap));
        // 排序后 AND
        must.sort(Comparator.comparingInt(RoaringBitmap::getCardinality));
        RoaringBitmap candidate = must.get(0).clone();
        for (int i = 1; i < must.size(); i++) {
            candidate.and(must.get(i));
            if (candidate.isEmpty()) {
                //这里可以优化，把 break直接改成return一个空list
                return Collections.emptyList();
            }
        }
        List<Integer> dspIds = new ArrayList<>(candidate.getCardinality());
        candidate.forEach((IntConsumer) dspIds::add);
        return dspIds;
    }

    private RoaringBitmap mergeBitmaps(List<String> keys, Map<String, RoaringBitmap> index) {
        List<RoaringBitmap> bitmaps = StreamUtils.toList(keys, index::get);
        return RoaringBitmap.or(bitmaps.iterator());
    }

    @Override
    public void removeDspById(int dspId) {
        // 使用更细粒度的锁，分别锁定每个map，而不是锁定整个对象
        log.info("Remove dsp from index, dspId: {}", dspId);
        removeDspFromIndex(adPlacementToDspIndex, null, dspId);
        removeDspFromIndex(osIndex, null, dspId);
        removeDspFromIndex(deviceTypeIndex, null, dspId);
        removeDspFromIndex(regionIndex, null, dspId);
    }

    @Override
    public void removeDsp(DspAggregate dspAggregate) {
        IndexKeys indexKeys = indexKeysBuilder.buildIndexKeys(dspAggregate);
        Integer dspId = dspAggregate.getDsp().getId();

        removeDspFromIndex(adPlacementToDspIndex, indexKeys.getTagIdKeys(), dspId);
        removeDspFromIndex(osIndex, indexKeys.getOsKeys(), dspId);
        removeDspFromIndex(deviceTypeIndex, indexKeys.getDeviceTypeKeys(), dspId);
        removeDspFromIndex(regionIndex, indexKeys.getRegionKeys(), dspId);
    }

    @Override
    public void clearIndex() {
        adPlacementToDspIndex.clear();
        osIndex.clear();
        deviceTypeIndex.clear();
        regionIndex.clear();
    }

    @Override
    public void indexLsc(LogSamplingConfig lsc) {
        if (lsc == null) {
            return;
        }
        int lscId = (int) lsc.getId();
        List<Integer> lscIndexKeys = buildLscIndexKeys(lsc);
        lscIndexKeys.forEach(lscIndexKey -> {
            lscIndex.computeIfAbsent(lscIndexKey, k -> new RoaringBitmap()).add(lscId);
        });
    }

    @Override
    public void removeLsc(LogSamplingConfig lsc) {
        List<Integer> lscIndexKeys = buildLscIndexKeys(lsc);
        removeLscFromIndex(lscIndex, lscIndexKeys, (int) lsc.getId());
    }

    private void removeLscFromIndex(ConcurrentMap<Integer, RoaringBitmap> index, List<Integer> keys, int lscId) {
        if (keys == null || keys.isEmpty()) {
            //如果要删除的key为空，则从索引中删除包含此dspId
            index.forEach((k, bitmap) -> removeValueFromIndexWithCow(index, lscId, k, bitmap));
            return;
        }
        keys.forEach(key -> removeValueFromIndexWithCow(index, lscId, key, index.get(key)));
    }

    private List<Integer> buildLscIndexKeys(LogSamplingConfig lsc) {
        Assert.notNull(lsc.getLogType(), "logType can not be null");
        int dspId = lsc.getDspId();
        int adSlotId = lsc.getAdSlotId();
        int mediaId = lsc.getMediaId();
        LogType logType = lsc.getLogType();

        String dspIdStr = dspId == 0 ? "" : String.valueOf(dspId);
        String adSlotIdStr = adSlotId == 0 ? "" : String.valueOf(adSlotId);
        String mediaIdStr = mediaId == 0 ? "" : String.valueOf(mediaId);

        List<Integer> keys = new ArrayList<>();

        if (dspId == 0) {
            // dspId为空的情况，匹配逻辑：
            // - 优先匹配logtype|mediaId|adSlotId
            // - 再次匹配：logtype|adSlotId
            // - 再次匹配 logtype|mediaId
            // - 最后匹配 logtype
            if (mediaId != 0 && adSlotId != 0) {
                keys.add(hashLscKey(logType.name(), mediaIdStr, adSlotIdStr));
            }
            if (adSlotId != 0) {
                keys.add(hashLscKey(logType.name(), adSlotIdStr));
            }
            if (mediaId != 0) {
                keys.add(hashLscKey(logType.name(), mediaIdStr));
            }
            keys.add(hashLscKey(logType.name()));
        } else {
            // dspId不为空的情况，匹配逻辑：
            // - logtype|mediaId|adSlotId|dspId
            // - logtype|adSlotId|dspId
            // - logtype|mediaId|dspId
            // - logtype|dspId
            // - logType
            if (mediaId != 0 && adSlotId != 0) {
                keys.add(hashLscKey(logType.name(), mediaIdStr, adSlotIdStr, dspIdStr));
            }
            if (adSlotId != 0) {
                keys.add(hashLscKey(logType.name(), adSlotIdStr, dspIdStr));
            }
            if (mediaId != 0) {
                keys.add(hashLscKey(logType.name(), mediaIdStr, dspIdStr));
            }
            keys.add(hashLscKey(logType.name(), dspIdStr));
            keys.add(hashLscKey(logType.name()));
        }

        return keys;
    }

    @Override
    public Integer getLscId(LogType logType, Integer mediaId, Integer adSlotId, Integer dspId) {
        //这里使用MurmurHash3算法来生成key, 即使有冲突也无所谓
        List<Integer> indexKeys = buildLscIndexKeys(logType, mediaId, adSlotId, dspId);
        for (Integer indexKey : indexKeys) {
            RoaringBitmap bitmap = lscIndex.get(indexKey);
            if (bitmap != null && !bitmap.isEmpty()) {
                return bitmap.iterator().next();
            }
        }
        return null;
    }

    private List<Integer> buildLscIndexKeys(LogType logType, Integer mediaId, Integer adSlotId, Integer dspId) {
        Assert.notNull(logType, "logType can not be null");

        return buildLscIndexKeys(LogSamplingConfig.newBuilder()
                .setLogType(logType)
                .setMediaId(mediaId == null ? 0 : mediaId)
                .setAdSlotId(adSlotId == null ? 0 : adSlotId)
                .setDspId(dspId == null ? 0 : dspId)
                .build());
    }

    private void removeDspFromIndex(Map<String, RoaringBitmap> index, List<String> keys, Integer dspId) {
        if (keys == null || keys.isEmpty()) {
            //如果要删除的key为空，则从索引中删除包含此dspId
            index.forEach((k, bitmap) -> removeValueFromIndexWithCow(index, dspId, k, bitmap));
            return;
        }
        keys.forEach(key -> removeValueFromIndexWithCow(index, dspId, key, index.get(key)));
    }

    private static <K extends Serializable> void removeValueFromIndexWithCow(Map<K, RoaringBitmap> index,
            Integer value,
            K k,
            RoaringBitmap bitmap) {
        index.computeIfPresent(k, (ignoreKey, existBitmap) -> {
            if (!bitmap.contains(value)) {
                return bitmap;
            }
            if (bitmap.getCardinality() == 1) {
                return null;
            }
            //使用 COW 模式，避免锁，因为索引更新的频率要远低于读频率
            RoaringBitmap newBitmap = bitmap.clone();
            newBitmap.remove(value);
            // 只有在删除后才进行优化，减少 CPU 开销
            newBitmap.runOptimize();
            return newBitmap;
        });
    }

    /**
     * 使用Guava MurmurHash3生成int类型的hash key
     */
    private int hashLscKey(String... parts) {
        String key = String.join("|", parts);
        return Hashing.murmur3_32_fixed().hashString(key, StandardCharsets.UTF_8).asInt();
    }
}