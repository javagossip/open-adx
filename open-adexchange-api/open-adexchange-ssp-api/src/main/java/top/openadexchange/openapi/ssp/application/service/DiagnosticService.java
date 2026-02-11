package top.openadexchange.openapi.ssp.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.constants.enums.DeviceType;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.application.dto.BuildIndexRequest;
import top.openadexchange.openapi.ssp.application.factory.IndexKeysBuilder;
import top.openadexchange.openapi.ssp.constants.CacheType;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Device;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp;

@Service
public class DiagnosticService {

    @Resource
    private MetadataCacheService metadataCacheService;
    @Resource
    private IndexKeysBuilder indexKeysBuilder;
    @Resource
    private OaxEngineServices oaxEngineServices;

    public Object inspectCacheData(String key, int type) {
        CacheType cacheType = CacheType.fromValue(type);
        switch (cacheType) {
            case DSP:
                if (NumberUtils.isDigits(key)) {
                    return metadataCacheService.getDsp(Integer.parseInt(key));
                }
                return metadataCacheService.getDspByDspId(key);
            case SITE:
                return metadataCacheService.getSite(Integer.parseInt(key));
            case AD_PLACEMENT:
                return metadataCacheService.getAdPlacement(Integer.parseInt(key));
            case SITE_AD_PLACEMENT:
                if (!NumberUtils.isDigits(key)) {
                    return metadataCacheService.getSiteAdPlacementByTagId(key);
                }
                return metadataCacheService.getSiteAdPlacementById(Integer.parseInt(key));
            case PUBLISHER:
                return metadataCacheService.getPublisher(Integer.parseInt(key));
        }
        return null;
    }

    public IndexKeys inspectDspIndex(String dspId) {
        Dsp dsp = metadataCacheService.getDspByDspId(dspId);
        DspAggregate dspAggregate = metadataCacheService.getDsp(dsp.getId());
        return indexKeysBuilder.buildIndexKeys(dspAggregate);
    }

    public List<Dsp> matchDsps(BuildIndexRequest buildIndexRequest) {
        BidRequest.Builder builder = buildBidRequest(buildIndexRequest);
        IndexKeys indexKeys = indexKeysBuilder.buildIndexKeys(builder);
        List<Integer> matchDspIds = oaxEngineServices.getIndexService().searchDsps(indexKeys);
        return metadataCacheService.getDsps(matchDspIds)
                .values()
                .stream()
                .map(DspAggregate::getDsp)
                .collect(Collectors.toList());
    }

    public IndexKeys buildIndexKeys(BuildIndexRequest buildIndexRequest) {
        BidRequest.Builder builder = buildBidRequest(buildIndexRequest);
        return indexKeysBuilder.buildIndexKeys(builder);
    }

    private BidRequest.Builder buildBidRequest(BuildIndexRequest buildIndexRequest) {
        BidRequest.Builder builder = BidRequest.newBuilder().setId(UUID.randomUUID().toString());
        builder.addImp(Imp.newBuilder().setId(UUID.randomUUID().toString()).setTagid(buildIndexRequest.adSlotId()));
        builder.setDevice(Device.newBuilder()
                .setIp(buildIndexRequest.ip())
                .setOs(buildIndexRequest.os())
                .setDeviceType(DeviceType.valueOf(buildIndexRequest.deviceType()).getValue()));
        return builder;
    }
}
