package top.openadexchange.repository;

import java.util.List;

import top.openadexchange.domain.entity.SiteAdPlacementAggregate;

public interface SiteAdPlacementAggregateRepository {
    SiteAdPlacementAggregate getSiteAdPlacementAggregate(Integer siteAdPlacementId);

    List<SiteAdPlacementAggregate> listByPageNo(int pageNo, int pageSize);
}
