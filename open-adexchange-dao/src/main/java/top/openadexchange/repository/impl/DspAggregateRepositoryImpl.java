package top.openadexchange.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspSiteAdPlacementDao;
import top.openadexchange.dao.DspTargetingDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspSiteAdPlacement;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.repository.DspAggregateRepository;

import static top.openadexchange.model.table.DspSiteAdPlacementTableDef.*;
import static top.openadexchange.model.table.DspTableDef.*;
import static top.openadexchange.model.table.DspTargetingTableDef.*;

@Repository
public class DspAggregateRepositoryImpl implements DspAggregateRepository {

    private static final int PAGE_SIZE = 100;

    @Autowired
    private DspDao dspDao;
    @Autowired
    private DspTargetingDao dspTargetingDao;
    @Autowired
    private DspSiteAdPlacementDao dspSiteAdPlacementDao;
    @Autowired
    private SiteAdPlacementDao siteAdPlacementDao;

    @Override
    public List<DspAggregate> listDspsByPageNo(int pageNo) {
        // 创建查询条件，只查询激活状态的DSP
        QueryWrapper queryWrapper = QueryWrapper.create().where(DSP.STATUS.eq(1)) // 只查询激活状态的DSP
                .orderBy(DSP.ID.asc()); // 按ID升序排列以保证分页结果的一致性

        // 执行分页查询
        Page<Dsp> dspPage = dspDao.page(Page.of(pageNo, PAGE_SIZE), queryWrapper);

        if (dspPage.getRecords().isEmpty()) {
            return new ArrayList<>();
        }

        // 获取当前页的DSP IDs
        List<Integer> dspIds = dspPage.getRecords().stream().map(Dsp::getId).collect(Collectors.toList());

        // 查询对应的定向信息和广告位信息
        Map<Integer, DspTargeting> dspTargetingMap = getDspTargetingMap(dspIds);
        Map<Integer, List<Integer>> dspSiteAdPlacementMap = getDspSiteAdPlacementMap(dspIds);

        // 组装聚合DTO
        return buildDspAggregates(dspPage.getRecords(), dspTargetingMap, dspSiteAdPlacementMap);
    }

    @Override
    public List<DspAggregate> getDspByIds(List<Integer> dspIds) {
        if (dspIds == null || dspIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询DSP信息
        List<Dsp> dsps = dspDao.list(QueryWrapper.create().where(DSP.ID.in(dspIds)));
        if (dsps.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取有效的DSP IDs
        List<Integer> validDspIds = dsps.stream().map(Dsp::getId).collect(Collectors.toList());

        // 查询对应的定向信息和广告位信息
        Map<Integer, DspTargeting> dspTargetingMap = getDspTargetingMap(validDspIds);
        Map<Integer, List<Integer>> dspSiteAdPlacementMap = getDspSiteAdPlacementMap(validDspIds);

        // 组装聚合DTO
        return buildDspAggregates(dsps, dspTargetingMap, dspSiteAdPlacementMap);
    }

    /**
     * 根据DSP IDs查询对应的定向信息
     */
    private Map<Integer, DspTargeting> getDspTargetingMap(List<Integer> dspIds) {
        Map<Integer, DspTargeting> dspTargetingMap = new HashMap<>();
        if (!dspIds.isEmpty()) {
            List<DspTargeting> dspTargetings =
                    dspTargetingDao.list(QueryWrapper.create().where(DSP_TARGETING.DSP_ID.in(dspIds)));
            for (DspTargeting targeting : dspTargetings) {
                dspTargetingMap.put(targeting.getDspId(), targeting);
            }
        }
        return dspTargetingMap;
    }

    /**
     * 根据DSP IDs查询对应的广告位信息
     */
    private Map<Integer, List<Integer>> getDspSiteAdPlacementMap(List<Integer> dspIds) {
        Map<Integer, List<Integer>> dspSiteAdPlacementMap = new HashMap<>();
        if (!dspIds.isEmpty()) {
            List<DspSiteAdPlacement> dspSiteAdPlacements =
                    dspSiteAdPlacementDao.list(QueryWrapper.create().where(DSP_SITE_AD_PLACEMENT.DSP_ID.in(dspIds)));
            for (DspSiteAdPlacement dspSiteAdPlacement : dspSiteAdPlacements) {
                dspSiteAdPlacementMap.computeIfAbsent(dspSiteAdPlacement.getDspId(), k -> new ArrayList<>())
                        .add(dspSiteAdPlacement.getSiteAdPlacementId());
            }
        }
        return dspSiteAdPlacementMap;
    }

    /**
     * 构建DSP聚合对象列表
     */
    private List<DspAggregate> buildDspAggregates(List<Dsp> dsps,
            Map<Integer, DspTargeting> dspTargetingMap,
            Map<Integer, List<Integer>> dspSiteAdPlacementMap) {
        List<DspAggregate> result = new ArrayList<>();
        for (Dsp dsp : dsps) {
            DspTargeting targeting = dspTargetingMap.get(dsp.getId());
            List<Integer> siteAdPlacementIds = dspSiteAdPlacementMap.getOrDefault(dsp.getId(), new ArrayList<>());
            if (siteAdPlacementIds.isEmpty()) {
                continue;
            }
            List<SiteAdPlacement> siteAdPlacements =
                    siteAdPlacementDao.list(QueryWrapper.create().in(SiteAdPlacement::getId, siteAdPlacementIds));
            DspAggregate aggregateDto = new DspAggregate(dsp, targeting, siteAdPlacementIds, siteAdPlacements);
            result.add(aggregateDto);
        }
        return result;
    }
}