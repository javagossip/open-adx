package top.openadexchange.constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Constants {

    public static final String DEFAULT_ALL_TARGETING = "*";
    public static final DateTimeFormatter REDIS_KEY_DATEFORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String formatNow() {
        return LocalDate.now().format(REDIS_KEY_DATEFORMAT);
    }
}
