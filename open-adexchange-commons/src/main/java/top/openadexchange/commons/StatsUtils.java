package top.openadexchange.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public static long calcMediaRevenue(long price, Integer revShare) {
        if (revShare == null || revShare <= 0) {
            return 0;
        }
        return new BigDecimal(price).multiply(new BigDecimal(revShare))
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                .longValue();
    }
}
