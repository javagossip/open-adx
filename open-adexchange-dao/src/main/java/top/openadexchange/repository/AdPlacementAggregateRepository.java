package top.openadexchange.repository;

import top.openadexchange.domain.entity.AdPlacementAggregate;

public interface AdPlacementAggregateRepository {

    AdPlacementAggregate getAdPlacementAggregate(Integer adPlacementId);
}
