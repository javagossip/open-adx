package top.openadexchange.openapi.ssp.application.service;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.util.CollectionUtils;

import org.springframework.util.StringUtils;

import top.openadexchange.constants.Constants;
import top.openadexchange.constants.enums.DeviceType;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspSiteAdPlacementDao;
import top.openadexchange.dao.DspTargetingDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspSiteAdPlacement;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest;

import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest.Imp;
import top.openadexchange.openapi.ssp.domain.gateway.IP2RegionService;
import top.openadexchange.openapi.ssp.domain.model.IpLocation;
import top.openadexchange.openapi.ssp.repository.DspAggregateRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

import static top.openadexchange.model.table.DspTableDef.DSP;
import static top.openadexchange.model.table.DspTargetingTableDef.DSP_TARGETING;
import static top.openadexchange.model.table.DspSiteAdPlacementTableDef.DSP_SITE_AD_PLACEMENT;

/**
 * DSP索引服务 使用RoaringBitmap建立DSP索引，根据广告位ID和定向条件进行快速匹配
 */
@Service
@Slf4j
public class DspIndexService {

    @Resource
    private DspAggregateRepository dspAggregateRepository;
    @Resource
    private IP2RegionService ip2RegionService;
    // 广告位ID到DSP ID列表的索引
    private final Map<String, RoaringBitmap> adPlacementToDspIndex = new ConcurrentHashMap<>();
    //广告定向信息到 DSP ID列表的索引
    // 操作系统->DSP ID索引列表
    private final Map<String, RoaringBitmap> osIndex = new ConcurrentHashMap<>();
    // 设备类型->DSP ID索引列表
    private final Map<String, RoaringBitmap> deviceTypeIndex = new ConcurrentHashMap<>();
    //区域定向->DSP ID索引列表
    private final Map<String, RoaringBitmap> regionIndex = new ConcurrentHashMap<>();

    /**
     * 初始化DSP索引
     */
    @PostConstruct
    public void initDspIndex() {
        log.info("开始初始化DSP索引...");
        try {
            buildDspIndex();
            log.info("DSP索引初始化完成");
        } catch (Exception ex) {
            log.error("DSP索引初始化失败", ex);
            throw new RuntimeException("DSP索引初始化失败", ex);
        }
    }

    /**
     * 构建DSP索引
     */
    private void buildDspIndex() {
        int pageNo = 1;
        while (true) {
            List<DspAggregate> dspAggregates = dspAggregateRepository.listDspsByPageNo(pageNo);
            if (dspAggregates.isEmpty()) {
                break;
            }
            dspAggregates.forEach(dspAggregate -> internalBuildDspIndex(dspAggregate));
        }
        log.info("DSP索引构建完成，涉及{}个广告位", adPlacementToDspIndex.size());
    }

    private void internalBuildDspIndex(DspAggregate dspAggregate) {
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

    public List<Integer> matchDspIds(AdFetchRequest request) {
        List<String> osKeys = new ArrayList<>(Arrays.asList(request.getDevice().getOs().toUpperCase(),
                Constants.DEFAULT_ALL_TARGETING));
        List<String> deviceTypeKeys =
                new ArrayList<>(Arrays.asList(DeviceType.fromValue(request.getDevice().getDeviceType()).name(),
                        Constants.DEFAULT_ALL_TARGETING));

        IpLocation ipLocation = ip2RegionService.getRegion(request.getDevice().getIp());
        List<String> regionKeys = new ArrayList<>(Arrays.asList(Constants.DEFAULT_ALL_TARGETING));
        if (ipLocation != null && ipLocation.getRegionCode() != null) {
            regionKeys.add(ipLocation.getRegionCode());
        }
        List<String> tagIdKeys = new ArrayList<>(Arrays.asList(Constants.DEFAULT_ALL_TARGETING));
        List<String> tagIds = request.getImpTagIds();
        if (!CollectionUtils.isEmpty(tagIds)) {
            tagIds.forEach(tagId -> tagIdKeys.add(tagId));
        }

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