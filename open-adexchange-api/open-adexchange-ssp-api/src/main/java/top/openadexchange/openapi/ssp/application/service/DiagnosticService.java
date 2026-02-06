package top.openadexchange.openapi.ssp.application.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.factory.BidRequestBuilder;
import top.openadexchange.openapi.ssp.application.factory.IndexKeysBuilder;
import top.openadexchange.openapi.ssp.constants.CacheType;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiagnosticService {

    @Resource
    private MetadataCacheService metadataCacheService;
    @Resource
    private IndexKeysBuilder indexKeysBuilder;
    @Resource
    private BidRequestBuilder bidRequestBuilder;
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
                return metadataCacheService.getSite(Long.parseLong(key));
            case AD_PLACEMENT:
                return metadataCacheService.getAdPlacement(Integer.parseInt(key));
            case SITE_AD_PLACEMENT:
                return metadataCacheService.getSiteAdPlacementById(Integer.parseInt(key));
            case PUBLISHER:
                return metadataCacheService.getPublisher(Long.parseLong(key));
        }
        return null;
    }

    public IndexKeys inspectDspIndex(String dspId) {
        Dsp dsp = metadataCacheService.getDspByDspId(dspId);
        DspAggregate dspAggregate = metadataCacheService.getDsp(dsp.getId());
        return indexKeysBuilder.buildIndexKeys(dspAggregate);
    }

    public List<Dsp> matchDsps(AdGetRequest adGetRequest) {
        BidRequest.Builder builder = bidRequestBuilder.buildBidRequest(adGetRequest);
        IndexKeys indexKeys = indexKeysBuilder.buildIndexKeys(builder);
        List<Integer> matchDspIds = oaxEngineServices.getIndexService().searchDsps(indexKeys);
        return metadataCacheService.getDsps(matchDspIds)
                .values()
                .stream()
                .map(DspAggregate::getDsp)
                .collect(Collectors.toList());
    }
}
