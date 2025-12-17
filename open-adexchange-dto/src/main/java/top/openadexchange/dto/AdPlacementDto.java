package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告位信息")
public class AdPlacementDto {
    @Schema(description = "广告位ID")
    private Long id;
    @Schema(description = "广告位名称")
    private String name;
    /**
     * banner,interstitial,native,video,rewarded,audio
     */
    @Schema(description = "广告位格式, banner,interstitial,native,video,rewarded,audio")
    private String adFormat;
    @Schema(description = "广告位宽度")
    private Integer width;
    @Schema(description = "广告位高度")
    private Integer height;

    /**
     * 广告位配置，不同格式的广告位属性不同,json格式
     */
    @Schema(description = "广告位配置,json格式")
    private String adSpecification;

    /**
     * 广告位底价
     */
    @Schema(description = "广告位底价")
    private BigDecimal floorPrice;

    /**
     * 结算币种
     */
    @Schema(description = "结算币种")
    private String currency;
    @Schema(description = "广告位状态, 1-正常, 0-禁用")
    private Integer status;
}
