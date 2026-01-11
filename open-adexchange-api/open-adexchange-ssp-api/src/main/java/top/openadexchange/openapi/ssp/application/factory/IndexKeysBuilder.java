package top.openadexchange.openapi.ssp.application.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import top.openadexchange.constants.Constants;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;
import top.openadexchange.openapi.ssp.domain.model.IpLocation;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;

@Component
public class IndexKeysBuilder {

    @Resource
    private OaxEngineServices oaxEngineServices;

    public IndexKeys buildIndexKeys(AdGetRequest request) {
        List<String> osKeys = new ArrayList<>();
        osKeys.add(request.getDevice().getOs().toUpperCase());
        osKeys.add(Constants.DEFAULT_ALL_TARGETING);

        List<String> deviceTypeKeys = new ArrayList<>();
        deviceTypeKeys.add(Constants.DEFAULT_ALL_TARGETING);

        IpLocation ipLocation = oaxEngineServices.getIp2RegionService().getRegion(request.getDevice().getIp());
        List<String> regionKeys = new ArrayList<>();
        regionKeys.add(Constants.DEFAULT_ALL_TARGETING);
        if (ipLocation != null && ipLocation.getRegionCode() != null) {
            regionKeys.add(ipLocation.getRegionCode());
        }
        List<String> tagIdKeys = new ArrayList<>();
        tagIdKeys.add(Constants.DEFAULT_ALL_TARGETING);
        List<String> tagIds = request.getImpTagIds();
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

    public IndexKeys buildIndexKeys(BidRequest request) {
        List<String> osKeys = new ArrayList<>();
        osKeys.add(request.getDevice().getOs().toUpperCase());
        osKeys.add(Constants.DEFAULT_ALL_TARGETING);

        List<String> deviceTypeKeys = new ArrayList<>();
        deviceTypeKeys.add(Constants.DEFAULT_ALL_TARGETING);

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
}
