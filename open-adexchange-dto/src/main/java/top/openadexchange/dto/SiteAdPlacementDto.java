package top.openadexchange.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.openadexchange.dto.util.FloorPriceUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "媒体广告位信息")
public class SiteAdPlacementDto {

    @Schema(description = "媒体广告位ID")
    private Integer id;

    @Schema(description = "站点/app id")
    private Integer siteId;

    @Schema(description = "关联广告位模板，支持同一个广告位关联多个广告位模板")
    private List<Integer> adPlacementIds;
    @Deprecated
    @Schema(description = "平台，ios,android,web,废弃字段，站点本身就包含了平台信息")
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
    @Schema(description = "是否开启调试模式, 默认关闭")
    private boolean debug;

    @JsonProperty("floorPrice")
    public String floorPriceAsString() {
        return FloorPriceUtils.centToYuan(floorPrice);
    }
}