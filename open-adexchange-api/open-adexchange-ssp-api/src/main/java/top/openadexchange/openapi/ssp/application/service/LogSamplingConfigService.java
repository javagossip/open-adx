package top.openadexchange.openapi.ssp.application.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogSamplingConfig;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogType;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;

@Service
public class LogSamplingConfigService {

    @Resource
    private OaxEngineServices oaxEngineServices;

    public int getLogSamplingRate(LogType logType, Integer mediaId, Integer adSlotId, Integer dspId) {
        IndexService indexService = oaxEngineServices.getIndexService();
        LogSamplingConfig lsc = indexService.getLsc(logType, mediaId, adSlotId, dspId);

        return lsc.getSamplingRate();
    }
}
