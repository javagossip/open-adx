package top.openadexchange.tracking.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RevenueUtils {

    private static final long MICRO_CENT = 1_000_000L;

    public static long calcMediaRevenue(long price, int revShare) {
        return price * MICRO_CENT * revShare / 100;
    }

    public static long calcAdxRevenue(long price, int revShare) {
        return price * MICRO_CENT * (100 - revShare) / 100;
    }

    public static long calcDspCost(long price, int revShare) {
        return price * MICRO_CENT;
    }
}
