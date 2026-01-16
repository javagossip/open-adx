package top.openadexchange.openapi.ssp.infra.repository;

import java.util.ArrayList;
import java.util.List;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;
import top.openadexchange.domain.entity.AdPlacementAggregate;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspPlacementMapping;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataRepository;

@Extension(keys = {"cache"})
public class CacheMetadataRepository implements MetadataRepository {

    @Resource
    private MetadataCacheService metadataCacheService;

    @Override
    public SiteAdPlacement getSiteAdPlacementByTagId(String tagId) {
        return metadataCacheService.getSiteAdPlacementByTagId(tagId);
    }

    @Override
    public Site getSite(Long siteId) {
        return metadataCacheService.getSite(siteId);
    }

    @Override
    public List<DspAggregate> getDspByIds(List<Integer> dspIds) {
        return new ArrayList<>(metadataCacheService.getDsps(dspIds).values());
    }

    @Override
    public AdPlacement getAdPlacement(Integer id) {
        return metadataCacheService.getAdPlacement(id);
    }

    @Override
    public Dsp getDspByDspId(String dspId) {
        return metadataCacheService.getDspByDspId(dspId);
    }

    @Override
    public AdPlacementAggregate getAdPlacementAggregate(Integer adPlacementId) {
        return metadataCacheService.getAdPlacementAggregate(adPlacementId);
    }

    @Override
    public DspPlacementMapping getDspPlacementMapping(Integer dspId, String tagid) {
        return metadataCacheService.getDsp(dspId).getDspPlacementMapping(tagid);
    }
}
