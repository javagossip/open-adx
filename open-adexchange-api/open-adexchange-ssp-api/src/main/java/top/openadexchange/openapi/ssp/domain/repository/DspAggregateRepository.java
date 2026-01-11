package top.openadexchange.openapi.ssp.domain.repository;

import java.util.List;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.domain.entity.DspAggregate;

@ExtensionPoint
public interface DspAggregateRepository {

    List<DspAggregate> listDspsByPageNo(int pageNo);

    List<DspAggregate> getDspByIds(List<Integer> dspIds);
}
