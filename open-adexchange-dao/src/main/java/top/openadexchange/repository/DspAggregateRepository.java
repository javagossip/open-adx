package top.openadexchange.repository;

import java.util.List;

import top.openadexchange.domain.entity.DspAggregate;

public interface DspAggregateRepository {

    List<DspAggregate> listDspsByPageNo(int pageNo);

    List<DspAggregate> getDspByIds(List<Integer> dspIds);
}
