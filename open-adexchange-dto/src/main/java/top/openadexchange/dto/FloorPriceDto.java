package top.openadexchange.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.openadexchange.commons.FloorPriceUtils;

@Data
@Schema(description = "媒体广告位底价信息")
public class FloorPriceDto {

    @Schema(description = "底价配置id")
    private Integer id;
    @Schema(description = "媒体广告位id")
    private Integer siteAdPlacementId;
    @Schema(description = "媒体广告位名称")
    private String siteAdPlacementName;
    @Schema(description = "媒体广告位底价")
    @JsonIgnore
    private Double floorPrice;
    @Schema(description = "行业id")
    private Integer industryId;
    @Schema(description = "行业名称")
    private String industryName;

    @Schema(description = "地域等级id")
    private Integer regionLevelId;
    @Schema(description = "地域等级名称")
    private String regionLevelName;

    @JsonProperty("floorPrice")
    public String getFloorPriceAsString() {
        return FloorPriceUtils.centToYuan(floorPrice);
    }
}
