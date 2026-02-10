package top.openadexchange.openapi.ssp.domain.gateway;

import java.util.List;
import java.util.Map;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.domain.entity.AdPlacementAggregate;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.domain.entity.SiteAdPlacementAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Publisher;
import top.openadexchange.model.Site;

@ExtensionPoint
public interface MetadataCacheService {

    void addDsp(DspAggregate dspAggregate);

    DspAggregate getDsp(Integer dspId);

    Map<Integer, DspAggregate> getDsps(List<Integer> dspIds);

    Site getSite(Long siteId);

    SiteAdPlacementAggregate getSiteAdPlacementByTagId(String tagId);

    AdPlacement getAdPlacement(Integer id);

    void addSite(Site site);

    void addSiteAdPlacement(SiteAdPlacementAggregate siteAdPlacement);

    void addAdPlacement(AdPlacement adPlacement);

    Dsp getDspByDspId(String dspId);

    AdPlacementAggregate getAdPlacementAggregate(Integer adPlacementId);

    void addAdPlacementAggregate(AdPlacementAggregate adPlacementAggregate);

    void removeDspById(int dspId);

    void removeDsp(DspAggregate dspAggregate);

    void removeSite(Long siteId);

    void removeAdPlacement(int adPlacementId);

    SiteAdPlacementAggregate getSiteAdPlacementById(int siteAdPlacementId);

    void removeSiteAdPlacement(int siteAdPlacementId);

    void removePublisher(Long publisherId);

    void addOrUpdatePublisher(Publisher publisher);

    Publisher getPublisher(Long publisherId);

    List<DspAggregate> getDspByIds(List<Integer> matchDspIds);
}
