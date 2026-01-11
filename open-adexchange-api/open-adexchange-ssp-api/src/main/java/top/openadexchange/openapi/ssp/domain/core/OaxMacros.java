package top.openadexchange.openapi.ssp.domain.core;

import java.util.regex.Pattern;

//广告交易平台宏定义以及处理
public class OaxMacros {

    public static final String START_MACRO = "{";
    public static final String END_MACRO = "}";

    public static final Pattern MACRO_PATTERN = Pattern.compile("\\{([A-Z0-9_]+)\\}");
    //加密竞价价格
    public static final String AUCTION_PRICE = "AUCTION_PRICE";
    //明文竞价价格
    public static final String AUCTION_PLAIN_PRICE = "AUCTION_PLAIN_PRICE";
}
