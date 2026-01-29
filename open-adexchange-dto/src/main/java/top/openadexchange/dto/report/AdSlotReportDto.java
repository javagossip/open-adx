package top.openadexchange.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 广告位报表DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告位报表数据")
public class AdSlotReportDto {

    @Schema(description = "广告位ID")
    private String adSlotId;

    @Schema(description = "广告位名称")
    private String adSlotName;

    @Schema(description = "站点/APP ID")
    private Long siteId;

    @Schema(description = "站点/APP名称")
    private String siteName;

    @Schema(description = "媒体ID")
    private Long publisherId;

    @Schema(description = "媒体名称")
    private String publisherName;

    @Schema(description = "曝光量")
    private Long impCount;

    @Schema(description = "点击量")
    private Long clickCount;

    @Schema(description = "点击率(%)")
    private BigDecimal clickRate;

    @Schema(description = "收入(元)")
    private BigDecimal revenue;
}
