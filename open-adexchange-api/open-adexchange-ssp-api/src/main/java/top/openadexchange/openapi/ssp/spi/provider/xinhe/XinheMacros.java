package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import java.util.regex.Pattern;

public final class XinheMacros {

    public static final String START_MACRO = "%%";
    public static final String END_MACRO = "%%";

    public static final Pattern MACRO_PATTERN = Pattern.compile("%%([A-Z0-9_]+)%%");
    //加密竞价价格
    public static final String WIN_PRICE = "WIN_PRICE";
}
