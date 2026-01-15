package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "dsp广告位映射查询条件")
public class DspPlacementMappingQuery {

    @Schema(description = "dsp编码")
    private String dspCode;
    @Schema(description = "媒体广告位编码")
    private String siteAdPlacementCode;
    @Schema(description = "dsp平台广告位编码")
    private String dspSlotId;
    @Schema(description = "分页页码")
    private int pageNo = 1;
    @Schema(description = "分页大小")
    private int pageSize = 20;
}
