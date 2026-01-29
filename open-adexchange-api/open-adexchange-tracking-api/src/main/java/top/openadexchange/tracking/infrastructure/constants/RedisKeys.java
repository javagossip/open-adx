package top.openadexchange.tracking.infrastructure.constants;

public final class RedisKeys {

    //媒体广告位，格式：imp:adslot:{yyyyMMdd}
    public static final String HASH_KEY_IMP_ADSLOT = "imp:adslot:%s";
    //媒体创意，格式：imp:crid:{yyyyMMdd}
    public static final String HASH_KEY_IMP_CRID = "imp:crid:%s";
    //媒体广告位点击，格式：clk:adslot:{yyyyMMdd}
    public static final String HASH_KEY_CLK_ADSLOT = "clk:adslot:%s";
    //媒体创意点击，格式：clk:crid:{yyyyMMdd}
    public static final String HASH_KEY_CLK_CRID = "clk:crid:%s";

    //媒体广告位曝光次数，格式：imp:adslot:{adslotId}:{yyyyMMdd}
    public static final String IMP_ADSLOT = "imp:adslot:%s:%s";
    //媒体广告位点击次数，格式：clk:adslot:{adslotId}:{yyyyMMdd}
    public static final String CLK_ADSLOT = "clk:adslot:%s:%s";
    //创意曝光次数，格式：imp:crid:{cridId}:{yyyyMMdd}
    public static final String IMP_CRID = "imp:crid:%s:%s";
    //创意点击次数，格式：clk:crid:{cridId}:{yyyyMMdd}
    public static final String CLK_CRID = "clk:crid:%s:%s";
    //广告曝光去重，格式：imp:dedup:{impId}
    public static final String IMP_DEDUP = "imp:dedup:%s";
    //广告点击去重，格式：clk:dedup:{impId}
    public static final String CLK_DEDUP = "clk:dedup:%s";

    //布隆过滤器，格式：bf:{type}:{yyyyMMdd}
    public static final String BLOOM_FILTER_IMP = "bf:imp:%s";
    public static final String BLOOM_FILTER_CLK = "bf:clk:%s";
}
