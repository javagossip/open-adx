package top.openadexchange.openapi.ssp.application.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.Constants;
import top.openadexchange.constants.enums.DeviceType;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;
import top.openadexchange.openapi.ssp.domain.model.IpLocation;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;

@Component
@Slf4j
public class IndexKeysBuilder {

    @Resource
    private OaxEngineServices oaxEngineServices;

    public IndexKeys buildIndexKeys(BidRequest.Builder request) {
        List<String> osKeys = new ArrayList<>();
        osKeys.add(request.getDevice().getOs().toUpperCase());
        osKeys.add(Constants.DEFAULT_ALL_TARGETING);

        List<String> deviceTypeKeys = new ArrayList<>();
        deviceTypeKeys.add(Constants.DEFAULT_ALL_TARGETING);
        DeviceType deviceType = DeviceType.fromValue(request.getDevice().getDeviceType());
        if (deviceType != null) {
            deviceTypeKeys.add(deviceType.name());
        }
        IpLocation ipLocation = oaxEngineServices.getIp2RegionService().getRegion(request.getDevice().getIp());
        List<String> regionKeys = new ArrayList<>();
        regionKeys.add(Constants.DEFAULT_ALL_TARGETING);
        if (ipLocation != null && ipLocation.getRegionCode() != null) {
            regionKeys.add(ipLocation.getRegionCode());
        }
        List<String> tagIdKeys = new ArrayList<>();
        tagIdKeys.add(Constants.DEFAULT_ALL_TARGETING);
        List<String> tagIds = request.getImpList().stream().map(imp -> imp.getTagid()).toList();
        if (!CollectionUtils.isEmpty(tagIds)) {
            tagIds.forEach(tagId -> tagIdKeys.add(tagId));
        }

        IndexKeys indexKeys = new IndexKeys();
        indexKeys.setOsKeys(osKeys);
        indexKeys.setDeviceTypeKeys(deviceTypeKeys);
        indexKeys.setRegionKeys(regionKeys);
        indexKeys.setTagIdKeys(tagIdKeys);
        return indexKeys;
    }

    public IndexKeys buildIndexKeys(DspAggregate dspAggregate) {
        if (dspAggregate == null) {
            return null;
        }
        Dsp dsp = dspAggregate.getDsp();
        if (dsp == null) {
            return null;
        }
        IndexKeys indexKeys = new IndexKeys();
        List<SiteAdPlacement> siteAdPlacements = dspAggregate.getDspSiteAdPlacments();
        if (!CollectionUtils.isEmpty(siteAdPlacements)) {
            List<String> tagIdKeys = new ArrayList<>();
            for (SiteAdPlacement siteAdPlacement : siteAdPlacements) {
                tagIdKeys.add(siteAdPlacement.getCode());
            }
            indexKeys.setTagIdKeys(tagIdKeys);
        } else {
            //不限广告位，则所有流量都发给这个DSP
            indexKeys.setTagIdKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
        }
        DspTargeting targeting = dspAggregate.getDspTargeting();
        if (targeting == null) {
            log.info("DSP:{}没有定向信息", dsp.getName());
            indexKeys.setOsKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
            indexKeys.setDeviceTypeKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
            indexKeys.setRegionKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
        } else {
            String osTargeting = targeting.getOs();
            String deviceTypeTargeting = targeting.getDeviceType();
            String regionTargeting = targeting.getRegion();
            if (StringUtils.hasText(osTargeting)) {
                List<String> osList = JSON.parseArray(osTargeting, String.class);
                if (osList == null || osList.isEmpty()) {
                    indexKeys.setOsKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
                } else {
                    indexKeys.setOsKeys(osList.stream().map(String::toUpperCase).toList());
                }
            } else {
                indexKeys.setOsKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
            }
            if (StringUtils.hasText(deviceTypeTargeting)) {
                List<String> deviceTypeList = JSON.parseArray(deviceTypeTargeting, String.class);
                if (deviceTypeList == null || deviceTypeList.isEmpty()) {
                    indexKeys.setDeviceTypeKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
                } else {
                    indexKeys.setDeviceTypeKeys(deviceTypeList.stream().map(String::toUpperCase).toList());
                }
            } else {
                indexKeys.setDeviceTypeKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
            }
            if (StringUtils.hasText(regionTargeting)) {
                List<String> regionList = JSON.parseArray(regionTargeting, String.class);
                if (regionList == null || regionList.isEmpty()) {
                    indexKeys.setRegionKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
                } else {
                    indexKeys.setRegionKeys(regionList.stream().map(String::toUpperCase).toList());
                }
            } else {
                indexKeys.setRegionKeys(Collections.singletonList(Constants.DEFAULT_ALL_TARGETING));
            }
        }
        return indexKeys;
    }
}
