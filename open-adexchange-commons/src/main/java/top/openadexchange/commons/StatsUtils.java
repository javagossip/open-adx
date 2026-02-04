package top.openadexchange.commons;

import java.math.BigDecimal;

public final class StatsUtils {

    private StatsUtils() {
    }

    public static BigDecimal getClickRate(Long clkCount, Long impCount) {
        if (impCount != null && impCount > 0) {
            return new BigDecimal(clkCount == null ? 0 : clkCount).divide(new BigDecimal(impCount),
                    4,
                    BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal getClickRateAsPercent(Long clkCount, Long impCount) {
        if (impCount != null && impCount > 0) {
            return new BigDecimal(clkCount == null ? 0 : clkCount).multiply(new BigDecimal(100))
                    .divide(new BigDecimal(impCount), 4, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
