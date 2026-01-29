package top.openadexchange.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 媒体报表DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "媒体报表数据")
public class PublisherReportDto {

    @Schema(description = "媒体ID")
    private Long publisherId;

    @Schema(description = "媒体名称")
    private String publisherName;

    @Schema(description = "媒体编码")
    private String publisherCode;

    @Schema(description = "曝光量")
    private Long impCount;

    @Schema(description = "点击量")
    private Long clickCount;

    @Schema(description = "点击率(%)")
    private BigDecimal clickRate;

    @Schema(description = "媒体收入(元)")
    private BigDecimal revenue;
}
