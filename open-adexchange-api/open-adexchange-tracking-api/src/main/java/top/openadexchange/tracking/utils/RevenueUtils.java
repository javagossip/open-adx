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

    public static void main(String[] args) {
        long mediaRevenue = calcMediaRevenue(1100, 90);
        long adxRevenue = calcAdxRevenue(1100, 90);
        long dspCost = calcDspCost(1100, 90);

        log.info("{}", mediaRevenue);
        log.info("{}", adxRevenue);
        log.info("{}", dspCost);
        log.info("{}", dspCost==(mediaRevenue+adxRevenue));
    }
}

