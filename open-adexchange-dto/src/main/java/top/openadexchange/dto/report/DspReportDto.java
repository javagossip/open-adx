package top.openadexchange.dto.report;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.openadexchange.commons.AmountSerializer;

@Data
@Schema(description = "DSP报表")
public class DspReportDto {

    private String dspId;
    private String dspCode;
    private String dspName;
    private Long reqCount;
    private Long bidCount;
    private Long winCount;
    private Long impCount;
    private Long clkCount;
    @Schema(description = "dsp成本（单位：元）")
    @JsonSerialize(using = AmountSerializer.class)
    private Long cost;
    private BigDecimal clickRate;
    private BigDecimal winRate;
    private Integer statDate;

    public BigDecimal getClickRate() {
        if (impCount != null && impCount > 0) {
            return new BigDecimal(clkCount == null ? 0 : clkCount).divide(new BigDecimal(impCount),
                    4,
                    BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getWinRate() {
        if (bidCount != null && bidCount > 0) {
            return new BigDecimal(winCount == null ? 0 : winCount).divide(new BigDecimal(bidCount),
                    4,
                    BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void incrReqCount(Long delta) {
        if (reqCount == null) {
            this.reqCount = delta;
        } else {
            this.reqCount += (delta == null ? 0 : delta);
        }
    }

    public void incrBidCount(Long delta) {
        if (bidCount == null) {
            this.bidCount = delta;
        } else {
            this.bidCount += (delta == null ? 0 : delta);
        }
    }

    public void incrWinCount(Long delta) {
        if (winCount == null) {
            this.winCount = delta;
        } else {
            this.winCount += (delta == null ? 0 : delta);
        }
    }

    public void incrImpCount(Long delta) {
        if (impCount == null) {
            this.impCount = delta;
        } else {
            this.impCount += (delta == null ? 0 : delta);
        }
    }

    public void incrClkCount(Long delta) {
        if (clkCount == null) {
            this.clkCount = delta;
        } else {
            this.clkCount += (delta == null ? 0 : delta);
        }
    }

    public void incrCost(Long delta) {
        if (cost == null) {
            this.cost = delta;
        } else {
            this.cost += (delta == null ? 0 : delta);
        }
    }
}
