package top.openadexchange.openapi.ssp.repository;

import java.util.List;

import top.openadexchange.domain.entity.DspAggregate;

public interface DspAggregateRepository {

    List<DspAggregate> listDspsByPageNo(int pageNo);
}
