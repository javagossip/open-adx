package top.openadexchange.openapi.ssp.application.service;

import java.util.List;

import com.mybatisflex.core.query.QueryWrapper;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.dao.AdPlacementDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.domain.repository.DspAggregateRepository;

//应用预热服务
@Service
public class ApplicationWarmupService {

    @Resource
    private DspAggregateRepository dspAggregateRepository;
    @Resource
    private MetadataCacheService metadataCacheService;
    @Resource
    private OaxEngineServices oaxEngineServices;
    @Resource
    private SiteDao siteDao;
    @Resource
    private AdPlacementDao adPlacementDao;
    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;

    public void warmup() {
        //初始化索引库以及缓存库
        initDspIndexAndCache();
        initMetadataCache();
    }

    private void initMetadataCache() {
        buildSiteCache();
        buildAdPlacementCache();
        buildSiteAdPlacementCache();
    }

    private void buildSiteAdPlacementCache() {
        int pageNo = 1;
        int pageSize = 100;
        while (true) {
            int offset = (pageNo - 1) * pageSize;
            List<SiteAdPlacement> siteAdPlacements = siteAdPlacementDao.list(QueryWrapper.create()
                    .eq(SiteAdPlacement::getStatus, 1)
                    .limit(offset, pageSize));
            if (siteAdPlacements.isEmpty()) {
                break;
            }
            siteAdPlacements.forEach(siteAdPlacement -> metadataCacheService.addSiteAdPlacement(siteAdPlacement));
            pageNo++;
        }
    }

    private void buildAdPlacementCache() {
        int pageNo = 1;
        int pageSize = 100;
        while (true) {
            int offset = (pageNo - 1) * pageSize;
            List<AdPlacement> adPlacements =
                    adPlacementDao.list(QueryWrapper.create().eq(AdPlacement::getStatus, 1).limit(offset, pageSize));
            if (adPlacements.isEmpty()) {
                break;
            }
            adPlacements.forEach(adPlacement -> metadataCacheService.addAdPlacement(adPlacement));
            pageNo++;
        }
    }

    private void buildSiteCache() {
        int pageNo = 1;
        int pageSize = 100;
        while (true) {
            int offset = (pageNo - 1) * pageSize;
            List<Site> siteList = siteDao.list(QueryWrapper.create().eq(Site::getStatus, 1).limit(offset, pageSize));
            if (siteList.isEmpty()) {
                break;
            }
            buildSiteCache(siteList);
            pageNo++;
        }
    }

    private void buildSiteCache(List<Site> siteList) {
        siteList.forEach(site -> metadataCacheService.addSite(site));
    }

    private void initDspIndexAndCache() {
        int pageNo = 1;
        while (true) {
            List<DspAggregate> dspAggregates = dspAggregateRepository.listDspsByPageNo(pageNo);
            if (dspAggregates.isEmpty()) {
                break;
            }
            buildDspIndex(dspAggregates);
            buildDspCache(dspAggregates);
            pageNo++;
        }
    }

    private void buildDspCache(List<DspAggregate> dspAggregates) {
        dspAggregates.forEach(dspAggregate -> metadataCacheService.addDsp(dspAggregate));
    }

    private void buildDspIndex(List<DspAggregate> dspAggregates) {
        IndexService indexService = oaxEngineServices.getIndexService();
        dspAggregates.forEach(dspAggregate -> indexService.indexDsp(dspAggregate));
    }
}
