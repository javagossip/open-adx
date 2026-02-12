package top.openadexchange.openapi.ssp.application.factory;

import top.openadexchange.oax.model.proto.OaxModelsProto;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogSamplingConfig;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogType;

public class LogSamplingConfigConverter {

    public static LogSamplingConfig convert(top.openadexchange.model.LogSamplingConfig logSamplingConfig) {
        OaxModelsProto.LogSamplingConfig.Builder builder = OaxModelsProto.LogSamplingConfig.newBuilder();

        builder.setId(logSamplingConfig.getId() == null ? 0 : logSamplingConfig.getId());
        builder.setLogType(logSamplingConfig.getLogType() == null
                ? LogType.GLOBAL
                : LogType.valueOf(logSamplingConfig.getLogType()));
        builder.setSamplingRate(logSamplingConfig.getSamplingRate() == null ? 0 : logSamplingConfig.getSamplingRate());
        builder.setDspId(logSamplingConfig.getDspId() == null ? 0 : logSamplingConfig.getDspId());
        builder.setAdSlotId(logSamplingConfig.getAdSlotId() == null ? 0 : logSamplingConfig.getAdSlotId());
        builder.setMediaId(logSamplingConfig.getMediaId() == null ? 0 : logSamplingConfig.getMediaId());

        return builder.build();
    }
}
