package top.openadexchange.dto.report;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.math.NumberUtils;

import top.openadexchange.commons.AmountSerializer;

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

    @Schema(description = "请求量")
    private Long reqCount;
    @Schema(description = "响应量")
    private Long bidCount;
    @Schema(description = "中标量")
    private Long winCount;
    @Schema(description = "曝光量")
    private Long impCount;

    @Schema(description = "点击量")
    private Long clickCount;

    @Schema(description = "点击率(%)")
    private BigDecimal clickRate;

    @Schema(description = "媒体收入(元)")
    @JsonSerialize(using = AmountSerializer.class)
    private Long revenue;
    @Schema(description = "adx平台收入(元)")
    @JsonSerialize(using = AmountSerializer.class)
    private Long adxRevenue;

    public void incrReqCount(Long count) {
        Long incrCount = count == null ? 0 : count;
        if (this.reqCount == null) {
            this.reqCount = incrCount;
        } else {
            this.reqCount += incrCount;
        }
    }

    public void incrBidCount(Long count) {
        Long incrCount = count == null ? 0 : count;
        if (this.bidCount == null) {
            this.bidCount = incrCount;
        } else {
            this.bidCount += incrCount;
        }
    }

    public void incrWinCount(Long count) {
        Long incrCount = count == null ? 0 : count;
        if (this.winCount == null) {
            this.winCount = incrCount;
        } else {
            this.winCount += incrCount;
        }
    }
    public void incrImpCount(Long count) {
        Long incrCount = count == null ? 0 : count;
        if (this.impCount == null) {
            this.impCount = incrCount;
        } else {
            this.impCount += incrCount;
        }
    }

    public void incrClickCount(Long count) {
        Long incrCount = count == null ? 0 : count;
        if (this.clickCount == null) {
            this.clickCount = incrCount;
        } else {
            this.clickCount += incrCount;
        }
    }

    public void incrRevenue(Long count) {
        Long incrCount = count == null ? 0 : count;
        if (this.revenue == null) {
            this.revenue = incrCount;
        } else {
            this.revenue += incrCount;
        }
    }

    public void incrAdxRevenue(Long count) {
        Long incrCount = count == null ? 0 : count;
        if (this.adxRevenue == null) {
            this.adxRevenue = incrCount;
        } else {
            this.adxRevenue += incrCount;
        }
    }

    public BigDecimal getClickRate() {
        return calcClickRate();
    }

    public BigDecimal calcClickRate() {
        BigDecimal clickRate;
        if (this.impCount != null && this.impCount > 0) {
            clickRate = new BigDecimal(this.clickCount).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(this.impCount), 4, BigDecimal.ROUND_HALF_UP);
        } else {
            clickRate = NumberUtils.createBigDecimal("0.0");
        }
        return clickRate;
    }
}
