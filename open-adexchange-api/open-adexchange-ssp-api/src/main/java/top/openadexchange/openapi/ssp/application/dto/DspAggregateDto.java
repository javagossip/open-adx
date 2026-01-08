package top.openadexchange.openapi.ssp.application.dto;

import lombok.Data;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspTargeting;

import java.util.List;

/**
 * DSP聚合信息DTO，包含DSP基础信息、定向信息和关联广告位信息
 */
@Data
public class DspAggregateDto {

    private Dsp dsp;
    private DspTargeting dspTargeting;
    private List<Long> siteAdPlacementIds;

    public DspAggregateDto(Dsp dsp, DspTargeting dspTargeting, List<Long> siteAdPlacementIds) {
        this.dsp = dsp;
        this.dspTargeting = dspTargeting;
        this.siteAdPlacementIds = siteAdPlacementIds;
    }
}