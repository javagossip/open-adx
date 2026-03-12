package top.openadexchange.openapi.ssp.domain.gateway;

import java.util.List;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogSamplingConfig;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogType;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;

@ExtensionPoint
public interface IndexService {

    void indexDsp(DspAggregate dspAggregate);

    void indexAdGroup(DspAggregate dspAggregate);

    List<Integer> searchDsps(IndexKeys indexKeys);

    void removeDspById(int dspId);

    void removeDsp(DspAggregate dspAggregate);

    void clearIndex();

    //索引日志采样配置索引构建
    void indexLsc(LogSamplingConfig lsc);

    void removeLsc(LogSamplingConfig lsc);

    Integer getLscId(LogType logType, Integer mediaId, Integer adSlotId, Integer dspId);
}
