package top.openadexchange.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.openadexchange.commons.FloorPriceUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "媒体广告位信息")
public class SiteAdPlacementDto {

    @Schema(description = "媒体广告位ID")
    private Integer id;

    @Schema(description = "站点/app id")
    private Long siteId;

    @Schema(description = "广告位id")
    private Integer adPlacementId;
    @Schema(description = "平台，ios,android,web")
    private String platform;

    @Schema(description = "site广告位名称")
    private String name;
    @Schema(description = "广告位编码")
    private String code;
    @Schema(description = "广告位底价（币种：CNY)")
    //@JsonIgnore
    private Double floorPrice;

    @Schema(description = "广告位截图url")
    private String demoUrl;

    @Schema(description = "状态, 1-使用中, 0-禁用")
    private Integer status;

    @JsonProperty("floorPrice")
    public String floorPriceAsString() {
        return FloorPriceUtils.centToYuan(floorPrice);
    }
}