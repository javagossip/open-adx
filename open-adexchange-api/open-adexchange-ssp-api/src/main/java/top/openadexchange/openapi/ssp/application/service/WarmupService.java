package top.openadexchange.openapi.ssp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.dao.AdPlacementDao;
import top.openadexchange.dao.LogSamplingConfigDao;
import top.openadexchange.dao.PublisherDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.domain.entity.AdPlacementAggregate;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.domain.entity.SiteAdPlacementAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.LogSamplingConfig;
import top.openadexchange.model.Publisher;
import top.openadexchange.model.Site;
import top.openadexchange.oax.model.proto.OaxModelsProto;
import top.openadexchange.openapi.ssp.application.factory.LogSamplingConfigConverter;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.repository.AdPlacementAggregateRepository;
import top.openadexchange.repository.DspAggregateRepository;
import top.openadexchange.repository.SiteAdPlacementAggregateRepository;

//应用预热服务
@Service
@Slf4j
public class WarmupService {

    @Resource
    private DspAggregateRepository dspAggregateRepository;
    @Resource
    private AdPlacementAggregateRepository adPlacementAggregateRepository;
    @Resource
    private SiteAdPlacementAggregateRepository siteAdPlacementAggregateRepository;
    @Resource
    private MetadataCacheService metadataCacheService;
    @Resource
    private OaxEngineServices oaxEngineServices;
    @Resource
    private SiteDao siteDao;
    @Resource
    private AdPlacementDao adPlacementDao;
    @Resource
    private PublisherDao publisherDao;
    @Resource
    private RateLimiterManager rateLimiterManager;
    @Resource
    private LogSamplingConfigDao logSamplingConfigDao;

    public void warmup() {
        //初始化索引库以及缓存库
        Thread.ofVirtual().name("warmup-thread").start(() -> {
            initDspIndexAndCache();
            initMetadataCache();
        });
    }

    private void initMetadataCache() {
        log.info("Init metadata cache...");
        buildPublisherCache();
        buildSiteCache();
        buildAdPlacementCache();
        buildSiteAdPlacementCache();
    }

    private void buildPublisherCache() {
        log.info("init publisher cache");
        int pageNo = 1;
        int pageSize = 100;
        while (true) {
            List<Publisher> publisherList = publisherDao.pageList(pageNo, pageSize);
            if (publisherList.isEmpty()) {
                break;
            }
            publisherList.forEach(publisher -> metadataCacheService.addOrUpdatePublisher(publisher));
            pageNo++;
        }
    }

    private void buildSiteAdPlacementCache() {
        int pageNo = 1;
        int pageSize = 100;
        while (true) {
            List<SiteAdPlacementAggregate> siteAdPlacements =
                    siteAdPlacementAggregateRepository.listByPageNo(pageNo, pageSize);
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
            initDspRateLimiters(dspAggregates);
            pageNo++;
        }
    }

    private void initDspRateLimiters(List<DspAggregate> dspAggregates) {
        if (dspAggregates == null || dspAggregates.isEmpty()) {
            return;
        }
        for (DspAggregate dspAggregate : dspAggregates) {
            Dsp dsp = dspAggregate.getDsp();
            if (dsp == null) {
                log.warn("Dsp is null, skip rate limiter init");
                continue;
            }
            String dspId = dsp.getDspId();
            if (!StringUtils.hasText(dspId)) {
                log.warn("DspId is null, skip rate limiter init, dspName: {}", dsp.getName());
                continue;
            }
            rateLimiterManager.updateLimiter(dspId, dsp.getQpsLimit());
        }
    }

    private void buildDspCache(List<DspAggregate> dspAggregates) {
        dspAggregates.forEach(dspAggregate -> metadataCacheService.addDsp(dspAggregate));
    }

    private void buildDspIndex(List<DspAggregate> dspAggregates) {
        IndexService indexService = oaxEngineServices.getIndexService();
        dspAggregates.forEach(dspAggregate -> indexService.indexDsp(dspAggregate));
    }

    public void updateAdPlacementById(Long entityId) {
        AdPlacementAggregate adPlacementAggregate =
                adPlacementAggregateRepository.getAdPlacementAggregate(entityId.intValue());
        if (adPlacementAggregate == null) {
            log.info("AdPlacement not found, remove cache, entityId: {}", entityId);
            metadataCacheService.removeAdPlacement(entityId.intValue());
            return;
        }
        log.info("Update adPlacement cache : {}", adPlacementAggregate);
        metadataCacheService.addAdPlacement(adPlacementAggregate.getAdPlacement());
        metadataCacheService.addAdPlacementAggregate(adPlacementAggregate);
    }

    public void updateDspById(Long entityId) {
        DspAggregate dspAggregate = dspAggregateRepository.getDspById(entityId.intValue());
        if (dspAggregate == null) {
            log.warn("Dsp not found, remove cache or index, entityId: {}", entityId);
            //这里获取原来的
            dspAggregate = metadataCacheService.getDsp(entityId.intValue());
            if (dspAggregate == null) {
                log.warn("Dsp not found in cache, entityId: {}", entityId);
                //防止缓存和索引数据不一致，这里再删除一次索引
                oaxEngineServices.getIndexService().removeDspById(entityId.intValue());
                return;
            }
            metadataCacheService.removeDsp(dspAggregate);
            oaxEngineServices.getIndexService().removeDsp(dspAggregate);
            rateLimiterManager.removeRateLimiter(dspAggregate.getDsp().getDspId());
            return;
        }
        log.info("Update DSP cache or index: {}", dspAggregate);
        metadataCacheService.addDsp(dspAggregate);
        oaxEngineServices.getIndexService().indexDsp(dspAggregate);
        //dsp信息变更，更新dsp qps限流器配置
        rateLimiterManager.updateLimiter(dspAggregate.getDsp().getDspId(), dspAggregate.getDsp().getQpsLimit());
    }

    public void updateSiteById(Integer entityId) {
        Site site = siteDao.getById(entityId);
        if (site == null || site.getStatus() == 0) {
            log.info("Site not found or not active, entityId: {}", entityId);
            metadataCacheService.removeSite(entityId);
            return;
        }
        log.info("Update site cache: {}", site);
        metadataCacheService.addSite(site);
    }

    public void updateSiteAdPlacementById(Long entityId) {
        SiteAdPlacementAggregate siteAdPlacement =
                siteAdPlacementAggregateRepository.getSiteAdPlacementAggregate(entityId.intValue());
        if (siteAdPlacement == null) {
            log.info("SiteAdPlacement not found or not active, entityId: {}", entityId);
            metadataCacheService.removeSiteAdPlacement(entityId.intValue());
        } else {
            log.info("Update siteAdPlacement cache: {}", siteAdPlacement);
            metadataCacheService.addSiteAdPlacement(siteAdPlacement);
        }
    }

    public void updatePublisherById(Integer publisherId) {
        Publisher publisher = publisherDao.getById(publisherId);
        if (publisher == null || publisher.getStatus() == 0) {
            log.info("Publisher not found or not active, entityId: {}", publisherId);
            metadataCacheService.removePublisher(publisherId);
            return;
        }
        log.info("Update publisher cache: {}", publisher);
        metadataCacheService.addOrUpdatePublisher(publisher);
    }

    public void updateLogSamplingConfigById(Long entityId) {
        log.info("Update logSamplingConfig cache: {}", entityId);
        LogSamplingConfig logSamplingConfig = logSamplingConfigDao.getById(entityId);
        if (logSamplingConfig == null || logSamplingConfig.getStatus() == 0) {
            log.info("LogSamplingConfig not found or not active,remove cache, entityId: {}", entityId);
            metadataCacheService.removeLogSamplingConfig(entityId);
            return;
        }
        OaxModelsProto.LogSamplingConfig logSamplingConfigProto =
                LogSamplingConfigConverter.convert(logSamplingConfig);
        metadataCacheService.updateLogSamplingConfigCache(logSamplingConfigProto);
    }
}
