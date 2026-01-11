package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;

import java.util.List;
import java.util.Map;

@ExtensionPoint
public interface MetadataCacheService {

    void addDsp(DspAggregate dspAggregate);

    DspAggregate getDsp(Integer dspId);

    Map<Integer,DspAggregate> getDsps(List<Integer> dspIds);

    Site getSite(Long siteId);

    SiteAdPlacement getSiteAdPlacementByTagId(String tagId);

    AdPlacement getAdPlacement(Integer id);

    void addSite(Site site);

    void addSiteAdPlacement(SiteAdPlacement siteAdPlacement);

    void addAdPlacement(AdPlacement adPlacement);

    Dsp getDspByDspId(String dspId);
}
