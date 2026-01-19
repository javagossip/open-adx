package top.openadexchange.repository;

import top.openadexchange.domain.entity.SiteAdPlacementAggregate;

import java.util.List;

public interface SiteAdPlacementAggregateRepository {
    SiteAdPlacementAggregate getSiteAdPlacementAggregate(Integer siteAdPlacementId);

    List<SiteAdPlacementAggregate> listByPageNo(int pageNo, int pageSize);
}
