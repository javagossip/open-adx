package top.openadexchange.openapi.ssp.application.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogSamplingConfig;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogType;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;

@Service
public class LogSamplingConfigService {

    @Resource
    private OaxEngineServices oaxEngineServices;
    @Resource
    private MetadataCacheService metadataCacheService;

    public int getLogSamplingRate(LogType logType, Integer mediaId, Integer adSlotId, Integer dspId) {
        IndexService indexService = oaxEngineServices.getIndexService();
        Integer lscId = indexService.getLscId(logType, mediaId, adSlotId, dspId);

        if (lscId == null) {
            return 0;
        }
        LogSamplingConfig lsc = metadataCacheService.getLogSamplingConfig(lscId);
        return lsc.getSamplingRate();
    }
}
