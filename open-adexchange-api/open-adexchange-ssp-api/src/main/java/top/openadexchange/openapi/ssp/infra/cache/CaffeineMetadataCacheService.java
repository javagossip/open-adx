package top.openadexchange.openapi.ssp.infra.cache;

import java.util.List;
import java.util.Map;

import com.chaincoretech.epc.annotation.Extension;
import com.github.benmanes.caffeine.cache.Cache;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.domain.entity.AdGroupAggregate;
import top.openadexchange.domain.entity.AdPlacementAggregate;
import top.openadexchange.domain.entity.CreativeAggregate;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.domain.entity.SiteAdPlacementAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Publisher;
import top.openadexchange.model.Site;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;

@Extension(keys = {"caffeine", "default"})
@Slf4j
public class CaffeineMetadataCacheService implements MetadataCacheService {

    @Resource
    private Cache<Integer, DspAggregate> dspCache;
    @Resource
    private Cache<String, Dsp> dspCacheByDspId;
    @Resource
    private Cache<Integer, AdGroupAggregate> adGroupCache;
    @Resource
    private Cache<String, SiteAdPlacementAggregate> siteAdPlacementCache;
    @Resource
    private Cache<Integer, SiteAdPlacementAggregate> siteAdPlacementCacheById;
    @Resource
    private Cache<Integer, AdPlacement> adPlacementCache;
    @Resource
    private Cache<Long, Site> siteCache;
    @Resource
    private Cache<Long, CreativeAggregate> creativeCache;
    @Resource
    private Cache<Integer, AdPlacementAggregate> adPlacementAggregateCache;
    @Resource
    private Cache<Long, Publisher> publisherCache;

    @Override
    public void addDsp(DspAggregate dspAggregate) {
        dspCache.put(dspAggregate.getDsp().getId(), dspAggregate);
        dspCacheByDspId.put(dspAggregate.getDsp().getDspId(), dspAggregate.getDsp());
    }

    @Override
    public DspAggregate getDsp(Integer dspId) {
        return dspCache.getIfPresent(dspId);
    }

    @Override
    public Map<Integer, DspAggregate> getDsps(List<Integer> dspIds) {
        return dspCache.getAllPresent(dspIds);
    }

    @Override
    public Site getSite(Long siteId) {
        return siteCache.getIfPresent(siteId);
    }

    @Override
    public SiteAdPlacementAggregate getSiteAdPlacementByTagId(String tagId) {
        return siteAdPlacementCache.getIfPresent(tagId);
    }

    @Override
    public AdPlacement getAdPlacement(Integer id) {
        return adPlacementCache.getIfPresent(id);
    }

    @Override
    public void addSite(Site site) {
        siteCache.put(site.getId(), site);
    }

    @Override
    public void addSiteAdPlacement(SiteAdPlacementAggregate siteAdPlacement) {
        log.info("Add siteAdPlacement cache: {}", siteAdPlacement);
        siteAdPlacementCache.put(siteAdPlacement.getSiteAdPlacement().getCode(), siteAdPlacement);
        siteAdPlacementCacheById.put(siteAdPlacement.getSiteAdPlacement().getId(), siteAdPlacement);
    }

    @Override
    public void addAdPlacement(AdPlacement adPlacement) {
        adPlacementCache.put(adPlacement.getId(), adPlacement);
    }

    @Override
    public Dsp getDspByDspId(String dspId) {
        return dspCacheByDspId.getIfPresent(dspId);
    }

    @Override
    public AdPlacementAggregate getAdPlacementAggregate(Integer adPlacementId) {
        return adPlacementAggregateCache.getIfPresent(adPlacementId);
    }

    @Override
    public void addAdPlacementAggregate(AdPlacementAggregate adPlacementAggregate) {
        adPlacementAggregateCache.put(adPlacementAggregate.getAdPlacement().getId(), adPlacementAggregate);
    }

    @Override
    public void removeDspById(int dspId) {
        DspAggregate dspAggregate = dspCache.getIfPresent(dspId);
        if (dspAggregate == null) {
            return;
        }
        String dspCode = dspAggregate.getDsp().getDspId();
        dspCache.invalidate(dspId);
        dspCacheByDspId.invalidate(dspCode);
    }

    @Override
    public void removeDsp(DspAggregate dspAggregate) {
        if (dspAggregate == null) {
            return;
        }
        dspCache.invalidate(dspAggregate.getDsp().getId());
        dspCacheByDspId.invalidate(dspAggregate.getDsp().getDspId());
    }

    @Override
    public void removeSite(Long siteId) {
        siteCache.invalidate(siteId);
    }

    @Override
    public void removeAdPlacement(int adPlacementId) {
        adPlacementCache.invalidate(adPlacementId);
        adPlacementAggregateCache.invalidate(adPlacementId);
    }

    @Override
    public SiteAdPlacementAggregate getSiteAdPlacementById(int siteAdPlacementId) {
        return siteAdPlacementCacheById.getIfPresent(siteAdPlacementId);
    }

    @Override
    public void removeSiteAdPlacement(int siteAdPlacementId) {
        SiteAdPlacementAggregate siteAdPlacement = siteAdPlacementCacheById.getIfPresent(siteAdPlacementId);
        if (siteAdPlacement != null) {
            siteAdPlacementCache.invalidate(siteAdPlacement.getSiteAdPlacement().getCode());
            siteAdPlacementCacheById.invalidate(siteAdPlacementId);
        }
    }

    @Override
    public void removePublisher(Long publisherId) {
        publisherCache.invalidate(publisherId);
    }

    @Override
    public void addOrUpdatePublisher(Publisher publisher) {
        publisherCache.put(publisher.getId(), publisher);
    }

    @Override
    public Publisher getPublisher(Long publisherId) {
        return publisherCache.getIfPresent(publisherId);
    }
}
