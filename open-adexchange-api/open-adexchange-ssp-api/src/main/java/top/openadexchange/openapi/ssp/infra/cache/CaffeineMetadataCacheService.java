package top.openadexchange.openapi.ssp.infra.cache;

import java.util.List;
import java.util.Map;

import com.chaincoretech.epc.annotation.Extension;
import com.github.benmanes.caffeine.cache.Cache;

import jakarta.annotation.Resource;
import top.openadexchange.domain.entity.AdGroupAggregate;
import top.openadexchange.domain.entity.AdPlacementAggregate;
import top.openadexchange.domain.entity.CreativeAggregate;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;

@Extension(keys = {"caffeine", "default"})
public class CaffeineMetadataCacheService implements MetadataCacheService {

    @Resource
    private Cache<Integer, DspAggregate> dspCache;
    @Resource
    private Cache<String, Dsp> dspCacheByDspId;
    @Resource
    private Cache<Integer, AdGroupAggregate> adGroupCache;
    @Resource
    private Cache<String, SiteAdPlacement> siteAdPlacementCache;
    @Resource
    private Cache<Integer, AdPlacement> adPlacementCache;
    @Resource
    private Cache<Long, Site> siteCache;
    @Resource
    private Cache<Long, CreativeAggregate> creativeCache;
    @Resource
    private Cache<Integer, AdPlacementAggregate> adPlacementAggregateCache;

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
    public SiteAdPlacement getSiteAdPlacementByTagId(String tagId) {
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
    public void addSiteAdPlacement(SiteAdPlacement siteAdPlacement) {
        siteAdPlacementCache.put(siteAdPlacement.getCode(), siteAdPlacement);
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
}
