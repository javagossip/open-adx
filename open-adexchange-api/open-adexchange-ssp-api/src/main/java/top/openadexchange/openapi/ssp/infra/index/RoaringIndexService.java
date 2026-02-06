package top.openadexchange.openapi.ssp.infra.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
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
        if (adPlacementBitmap.getCardinality() == 0) {
            return Collections.emptyList();
        }
        RoaringBitmap osBitmap = mergeBitmaps(osKeys, osIndex);
        if (osBitmap.getCardinality() == 0) {
            return Collections.emptyList();
        }
        RoaringBitmap deviceTypeBitmap = mergeBitmaps(deviceTypeKeys, deviceTypeIndex);
        if (deviceTypeBitmap.getCardinality() == 0) {
            return Collections.emptyList();
        }
        RoaringBitmap regionBitmap = mergeBitmaps(regionKeys, regionIndex);
        if (regionBitmap.getCardinality() == 0) {
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
                break;
            }
        }
        List<Integer> dspIds = new ArrayList<>(candidate.getCardinality());
        IntIterator it = candidate.getIntIterator();
        while (it.hasNext()) {
            dspIds.add(it.next());
        }
        return dspIds;
    }

    private RoaringBitmap mergeBitmaps(List<String> keys, Map<String, RoaringBitmap> index) {
        List<RoaringBitmap> bitmaps = keys.stream().map(index::get).filter(Objects::nonNull).toList();
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

    private void removeDspFromIndex(Map<String, RoaringBitmap> index, List<String> keys, Integer dspId) {
        if (keys == null || keys.isEmpty()) {
            //如果要删除的key为空，则从索引中删除包含此dspId
            index.forEach((k, bitmap) -> {
                removeValueFromIndexWithCow(index, dspId, k, bitmap);
            });
            return;
        }
        keys.forEach(key -> {
            removeValueFromIndexWithCow(index, dspId, key, index.get(key));
        });
    }

    private static void removeValueFromIndexWithCow(Map<String, RoaringBitmap> index,
            Integer dspId,
            String k,
            RoaringBitmap bitmap) {
        index.computeIfPresent(k, (ignoreKey, existBitmap) -> {
            if (!bitmap.contains(dspId)) {
                return bitmap;
            }
            if (bitmap.getCardinality() == 1) {
                return null;
            }
            //使用 COW 模式，避免锁，因为索引更新的频率要远低于读频率
            RoaringBitmap newBitmap = bitmap.clone();
            newBitmap.remove(dspId);
            // 只有在删除后才进行优化，减少 CPU 开销
            newBitmap.runOptimize();
            return newBitmap;
        });
    }
}