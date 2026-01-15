package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DspPlacementMappingDto {

    @Schema(description = "映射ID")
    private Integer id;
    @Schema(description = "媒体广告位ID")
    private Integer siteAdPlacementId; //媒体广告位 ID
    @Schema(description = "媒体广告位名称")
    private String siteAdPlacementName; //媒体广告位名称
    @Schema(description = "媒体广告位编码")
    private String siteAdPlacementCode; //媒体广告位编码
    @Schema(description = "dsp广告位编码")
    private String dspSlotId; //dsp广告位编码
    @Schema(description = "dspID")
    private Integer dspId;
    @Schema(description = "dsp编码")
    private String dspCode; //dsp平台编码
    @Schema(description = "dsp名称")
    private String dspName; //dsp平台名称
}
