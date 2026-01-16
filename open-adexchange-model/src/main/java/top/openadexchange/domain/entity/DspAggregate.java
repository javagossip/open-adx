package top.openadexchange.domain.entity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Data;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspPlacementMapping;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;

/**
 * DSP聚合信息DTO，包含DSP基础信息、定向信息和关联广告位信息
 */
@Data
public class DspAggregate {

    private Dsp dsp;
    private DspTargeting dspTargeting;
    private List<Integer> siteAdPlacementIds;
    private List<SiteAdPlacement> dspSiteAdPlacments;
    private List<DspPlacementMapping> dspPlacementMappings;
    //<siteAdPlacementCode,dspSlotId>
    private Map<String, DspPlacementMapping> sapCodeDspSlotIdMapping;
    private Map<Integer, SiteAdPlacement> siteAdPlacementIdSiteAdPlacementMapping;

    public DspAggregate(Dsp dsp,
            DspTargeting dspTargeting,
            List<Integer> siteAdPlacementIds,
            List<SiteAdPlacement> siteAdPlacements,
            List<DspPlacementMapping> dspPlacementMappings) {
        this.dsp = dsp;
        this.dspTargeting = dspTargeting;
        this.siteAdPlacementIds = siteAdPlacementIds;
        this.dspSiteAdPlacments = siteAdPlacements;

        if (dspPlacementMappings != null && dspPlacementMappings.size() > 0) {
            siteAdPlacementIdSiteAdPlacementMapping = dspSiteAdPlacments.stream()
                    .collect(Collectors.toMap(SiteAdPlacement::getId, siteAdPlacement -> siteAdPlacement));
            sapCodeDspSlotIdMapping = dspPlacementMappings.stream()
                    .collect(Collectors.toMap(dspPlacementMapping -> siteAdPlacementIdSiteAdPlacementMapping.get(
                            dspPlacementMapping.getSiteAdPlacementId()).getCode(), Function.identity(), (a, b) -> b));
        }
    }

    public DspPlacementMapping getDspPlacementMapping(String tagid) {
        return sapCodeDspSlotIdMapping.get(tagid);
    }
}