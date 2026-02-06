package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import java.util.regex.Pattern;

public final class XinheMacros {

    public static final String START_MACRO = "%%";
    public static final String END_MACRO = "%%";

    public static final Pattern MACRO_PATTERN = Pattern.compile("%%([A-Z0-9_]+)%%");
    public static final Pattern MACRO_PATTERN_1 = Pattern.compile("__([A-Z0-9_]+)__");
    //加密竞价价格
    public static final String WIN_PRICE = "WIN_PRICE";
    public static final String TS = "TS";
    public static final Pattern[] MACRO_PATTERNS = {MACRO_PATTERN, MACRO_PATTERN_1};
}
