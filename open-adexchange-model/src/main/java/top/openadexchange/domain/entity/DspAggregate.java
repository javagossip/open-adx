package top.openadexchange.domain.entity;

import java.util.List;

import lombok.Data;
import top.openadexchange.model.Dsp;
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

    public DspAggregate(Dsp dsp,
            DspTargeting dspTargeting,
            List<Integer> siteAdPlacementIds,
            List<SiteAdPlacement> siteAdPlacements) {
        this.dsp = dsp;
        this.dspTargeting = dspTargeting;
        this.siteAdPlacementIds = siteAdPlacementIds;
        this.dspSiteAdPlacments = siteAdPlacements;
    }
}