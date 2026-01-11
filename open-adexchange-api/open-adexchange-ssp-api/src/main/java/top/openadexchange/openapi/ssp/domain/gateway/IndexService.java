package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;

import java.util.List;

@ExtensionPoint
public interface IndexService {

    void indexDsp(DspAggregate dspAggregate);

    void indexAdGroup(DspAggregate dspAggregate);

    List<Integer> searchDsps(IndexKeys indexKeys);
}
