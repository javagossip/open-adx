package top.openadexchange.constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Constants {

    public static final String DEFAULT_ALL_TARGETING = "*";
    public static final DateTimeFormatter REDIS_KEY_DATEFORMAT = DateTimeFormatter.ofPattern("yyyyMMddHH");

    public static String formatNow() {
        return LocalDateTime.now().format(REDIS_KEY_DATEFORMAT);
    }
}
