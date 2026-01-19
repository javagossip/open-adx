package top.openadexchange.repository.impl;

import com.mybatisflex.core.query.QueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.springframework.util.Assert;

import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteAdpAdtMappingDao;
import top.openadexchange.domain.entity.SiteAdPlacementAggregate;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.model.SiteAdpAdtMapping;
import top.openadexchange.repository.SiteAdPlacementAggregateRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SiteAdPlacementAggregateRepositoryImpl implements SiteAdPlacementAggregateRepository {

    @Autowired
    private SiteAdPlacementDao siteAdPlacementDao;
    @Autowired
    private SiteAdpAdtMappingDao siteAdpAdtMappingDao;

    @Override
    public SiteAdPlacementAggregate getSiteAdPlacementAggregate(Integer siteAdPlacementId) {
        Assert.notNull(siteAdPlacementId, "siteAdPlacementId cannot be null");

        SiteAdPlacementAggregate siteAdPlacementAggregate = new SiteAdPlacementAggregate();
        siteAdPlacementAggregate.setSiteAdPlacement(siteAdPlacementDao.getById(siteAdPlacementId));
        siteAdPlacementAggregate.setAdPlacementIds(siteAdpAdtMappingDao.getAdpAdtMappingIds(siteAdPlacementId));
        return siteAdPlacementAggregate;
    }

    @Override
    public List<SiteAdPlacementAggregate> listByPageNo(int pageNo, int pageSize) {
        List<SiteAdPlacement> siteAdPlacements = siteAdPlacementDao.list(QueryWrapper.create()
                .eq(SiteAdPlacement::getStatus, 1)
                .limit((pageNo - 1) * pageSize, pageSize));
        if (siteAdPlacements.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> siteAdPlacementIds = siteAdPlacements.stream().map(SiteAdPlacement::getId).toList();
        List<SiteAdpAdtMapping> siteAdpAdtMappings = siteAdpAdtMappingDao.listBySiteAdPlacementIds(siteAdPlacementIds);
        Map<Integer, List<Integer>> siteAdPlacementId2AdPlacementIds = siteAdpAdtMappings.stream()
                .collect(Collectors.groupingBy(SiteAdpAdtMapping::getSiteAdPlacementId,
                        Collectors.mapping(SiteAdpAdtMapping::getAdPlacementId, Collectors.toList())));
        return siteAdPlacements.stream().map(siteAdPlacement -> {
            SiteAdPlacementAggregate siteAdPlacementAggregate = new SiteAdPlacementAggregate();
            siteAdPlacementAggregate.setSiteAdPlacement(siteAdPlacement);
            siteAdPlacementAggregate.setAdPlacementIds(siteAdPlacementId2AdPlacementIds.get(siteAdPlacement.getId()));
            return siteAdPlacementAggregate;
        }).collect(Collectors.toList());
    }
}
