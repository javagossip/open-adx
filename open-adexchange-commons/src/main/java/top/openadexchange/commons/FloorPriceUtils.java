package top.openadexchange.commons;

import java.math.BigDecimal;

public final class FloorPriceUtils {

    private FloorPriceUtils() {
    }

    public static String centToYuan(Double floorPrice) {
        if (floorPrice == null) {
            return null;
        }
        return BigDecimal.valueOf(floorPrice).divide(BigDecimal.valueOf(100)).setScale(2).toString();
    }

    public static Long yuanToCent(Double floorPrice) {
        if (floorPrice == null) {
            return null;
        }
        return BigDecimal.valueOf(floorPrice).multiply(BigDecimal.valueOf(100)).setScale(0).longValue();
    }

    public static Double centToYuanDouble(Long floorPrice) {
        if (floorPrice == null) {
            return null;
        }
        return BigDecimal.valueOf(floorPrice).divide(BigDecimal.valueOf(100)).setScale(2).doubleValue();
    }
}
