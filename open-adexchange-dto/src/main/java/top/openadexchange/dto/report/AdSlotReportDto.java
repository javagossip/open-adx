package top.openadexchange.dto.report;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.openadexchange.commons.AmountSerializer;
import top.openadexchange.commons.StatsUtils;

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

    @Schema(description = "收入(元)")
    @JsonSerialize(using = AmountSerializer.class)
    private Long revenue;
    @Schema(description = "adx平台收入(元)")
    @JsonSerialize(using = AmountSerializer.class)
    private Long adxRevenue;

    public void incrReqCount(Long reqCount) {
        if (this.reqCount == null) {
            this.reqCount = reqCount;
        } else {
            this.reqCount += (reqCount == null ? 0 : reqCount);
        }
    }

    public void incrBidCount(Long bidCount) {
        if (this.bidCount == null) {
            this.bidCount = bidCount;
        } else {
            this.bidCount += (bidCount == null ? 0 : bidCount);
        }
    }

    public void incrWinCount(Long winCount) {
        if (this.winCount == null) {
            this.winCount = winCount;
        } else {
            this.winCount += (winCount == null ? 0 : winCount);
        }
    }

    public void incrImpCount(Long impCount) {
        if (this.impCount == null) {
            this.impCount = impCount;
        } else {
            this.impCount += (impCount == null ? 0 : impCount);
        }
    }

    public void incrClickCount(Long clickCount) {
        if (this.clickCount == null) {
            this.clickCount = clickCount;
        } else {
            this.clickCount += (clickCount == null ? 0 : clickCount);
        }
    }

    public void incrRevenue(Long revenue) {
        if (this.revenue == null) {
            this.revenue = revenue;
        } else {
            this.revenue += (revenue == null ? 0 : revenue);
        }
    }

    public void incrAdxRevenue(Long adxRevenue) {
        if (this.adxRevenue == null) {
            this.adxRevenue = adxRevenue;
        } else {
            this.adxRevenue += (adxRevenue == null ? 0 : adxRevenue);
        }
    }

    public BigDecimal getClickRate() {
        return StatsUtils.getClickRateAsPercent(clickCount, impCount);
    }
}
