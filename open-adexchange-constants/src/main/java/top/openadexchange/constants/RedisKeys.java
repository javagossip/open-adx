package top.openadexchange.constants;

import java.util.Arrays;
import java.util.List;

public final class RedisKeys {

    // 媒体广告位相关统计数据，格式：adslot:{adslotId}:{yyyyMMdd}
    public static final String STAT_ADSLOT = "adslot:%s:%s";
    // DSP相关统计数据，格式：dsp:{dspId}:{yyyyMMdd}
    public static final String STAT_DSP = "dsp:%s:%s";
    // 创意相关统计数据，格式：crid:{cridId}:{yyyyMMdd}
    public static final String STAT_CRID = "crid:%s:%s";
    // 广告位set(每日)，格式：adslots:{yyyyMMdd}
    public static final String SET_ADSLOTS = "adslots:%s";
    // DSP set(每日)，格式：dsps:{yyyyMMdd}
    public static final String SET_DSPS = "dsps:%s";

    // 布隆过滤器，格式：bf:{type}:{yyyyMMdd}
    public static final String BLOOM_FILTER_IMP = "bf:imp:%s";
    public static final String BLOOM_FILTER_CLK = "bf:clk:%s";

    public static final String HASH_FIELD_IMP = "imp";
    public static final String HASH_FIELD_CLK = "clk";
    public static final String HASH_FIELD_BID = "bid";
    public static final String HASH_FIELD_WIN = "win";
    public static final String HASH_FIELD_REQ = "req";
    public static final String HASH_FIELD_PRICE = "price";
    public static final String HASH_FIELD_REVENUE = "revenue";
    public static final String HASH_FIELD_DSP_COST = "dsp_cost";
    public static final String HASH_FIELD_ADX_REVENUE = "adx_revenue";

    public static final List HASH_FIELDS = Arrays.asList(HASH_FIELD_IMP,
            HASH_FIELD_CLK,
            HASH_FIELD_BID,
            HASH_FIELD_WIN,
            HASH_FIELD_REQ,
            HASH_FIELD_REVENUE,
            HASH_FIELD_DSP_COST,
            HASH_FIELD_ADX_REVENUE);

    public static String keyStatDsp(String dspId) {
        return String.format(STAT_DSP, dspId, Constants.formatNow());
    }

    public static String keyStatDsp(String dspId, String syncDate) {
        return String.format(STAT_DSP, dspId, syncDate);
    }

    public static String keyStatAdSlot(String adSlotId) {
        return String.format(STAT_ADSLOT, adSlotId, Constants.formatNow());
    }

    public static String keyStatAdSlot(String adSlotId, String statDate) {
        return String.format(STAT_ADSLOT, adSlotId, statDate);
    }

    public static String keyStatCrid(String cridId) {
        return String.format(STAT_CRID, cridId, Constants.formatNow());
    }

    public static String keyStatAdslots() {
        return String.format(SET_ADSLOTS, Constants.formatNow());
    }

    public static String keyStatAdslots(String statDate) {
        return String.format(SET_ADSLOTS, statDate);
    }

    public static String keyStatDsps() {
        return String.format(SET_DSPS, Constants.formatNow());
    }

    public static String keyStatDsps(String statDate) {
        return String.format(SET_DSPS, statDate);
    }
}
