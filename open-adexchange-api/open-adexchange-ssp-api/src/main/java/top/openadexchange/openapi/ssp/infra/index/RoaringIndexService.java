package top.openadexchange.openapi.ssp.infra.index;

import com.alibaba.fastjson2.JSON;
import com.chaincoretech.epc.annotation.Extension;

import lombok.extern.slf4j.Slf4j;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import top.openadexchange.constants.Constants;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

    @Override
    public void indexDsp(DspAggregate dspAggregate) {
        DspTargeting targeting = dspAggregate.getDspTargeting();
        Dsp dsp = dspAggregate.getDsp();
        List<SiteAdPlacement> siteAdPlacements = dspAggregate.getDspSiteAdPlacments();
        if (!CollectionUtils.isEmpty(siteAdPlacements)) {
            siteAdPlacements.forEach(siteAdPlacement -> adPlacementToDspIndex.computeIfAbsent(siteAdPlacement.getCode(),
                    key -> new RoaringBitmap()).add(dsp.getId()));
        } else {
            //不限广告位，则所有流量都发给这个DSP
            adPlacementToDspIndex.computeIfAbsent(Constants.DEFAULT_ALL_TARGETING, key -> new RoaringBitmap())
                    .add(dsp.getId());
        }
        if (targeting == null) {
            log.info("DSP:{}没有定向信息", dsp.getName());
            osIndex.computeIfAbsent(Constants.DEFAULT_ALL_TARGETING, key -> new RoaringBitmap()).add(dsp.getId());
            deviceTypeIndex.computeIfAbsent(Constants.DEFAULT_ALL_TARGETING, key -> new RoaringBitmap())
                    .add(dsp.getId());
            regionIndex.computeIfAbsent(Constants.DEFAULT_ALL_TARGETING, key -> new RoaringBitmap()).add(dsp.getId());
            return;
        }
        //如果dsp有os定向，则只有特定os的流量会发给这个dsp
        if (StringUtils.hasText(targeting.getOs())) {
            List<String> osList = JSON.parseArray(targeting.getOs(), String.class);
            osList.forEach(os -> osIndex.computeIfAbsent(os.toUpperCase(), key -> new RoaringBitmap())
                    .add(dsp.getId()));
        } else {
            //不限os
            osIndex.computeIfAbsent(Constants.DEFAULT_ALL_TARGETING, key -> new RoaringBitmap()).add(dsp.getId());
        }
        //设备类型定向
        if (StringUtils.hasText(targeting.getDeviceType())) {
            List<String> deviceTypes = JSON.parseArray(targeting.getDeviceType(), String.class);
            deviceTypes.forEach(deviceType -> deviceTypeIndex.computeIfAbsent(deviceType.toUpperCase(),
                    key -> new RoaringBitmap()).add(dsp.getId()));
        } else {
            //不限设备类型
            deviceTypeIndex.computeIfAbsent(Constants.DEFAULT_ALL_TARGETING, key -> new RoaringBitmap())
                    .add(dsp.getId());
        }
        //区域定向
        if (StringUtils.hasText(targeting.getRegion())) {
            List<String> regions = JSON.parseArray(targeting.getRegion(), String.class);
            regions.forEach(region -> {
                //regionCode是 6位数字，如 100000，支持省、市、县三级定向，如果dsp定向区域是 100000, 用户访问流量区域是 101100，则该DSP会收到该流量
                regionIndex.computeIfAbsent(region, key -> new RoaringBitmap()).add(dsp.getId()); //县
            });
        } else {
            //不限区域
            regionIndex.computeIfAbsent(Constants.DEFAULT_ALL_TARGETING, key -> new RoaringBitmap()).add(dsp.getId());
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

        List<RoaringBitmap> adPlacementBitmaps =
                tagIdKeys.stream().map(adPlacementToDspIndex::get).filter(Objects::nonNull).toList();
        RoaringBitmap adPlacementBitmap = RoaringBitmap.or(adPlacementBitmaps.iterator());

        List<RoaringBitmap> osBitmaps = osKeys.stream().map(osIndex::get).filter(Objects::nonNull).toList();
        RoaringBitmap osBitmap = RoaringBitmap.or(osBitmaps.iterator());

        List<RoaringBitmap> deviceTypeBitmaps =
                deviceTypeKeys.stream().map(deviceTypeIndex::get).filter(Objects::nonNull).toList();
        RoaringBitmap deviceTypeBitmap = RoaringBitmap.or(deviceTypeBitmaps.iterator());

        List<RoaringBitmap> regionBitmaps = regionKeys.stream().map(regionIndex::get).filter(Objects::nonNull).toList();
        RoaringBitmap regionBitmap = RoaringBitmap.or(regionBitmaps.iterator());

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
}
