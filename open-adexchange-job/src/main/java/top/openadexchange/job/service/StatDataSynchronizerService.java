package top.openadexchange.job.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mybatisflex.core.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import org.springframework.util.StringUtils;

import top.openadexchange.constants.Constants;
import top.openadexchange.constants.RedisKeys;
import top.openadexchange.dao.AdSlotStatDao;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspStatDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.job.handler.StatDataSynchronizer.StatDataSynchronizerParam;
import top.openadexchange.model.AdSlotStat;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspStat;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;

@Service
@Slf4j
public class StatDataSynchronizerService {

    private static final int BATCH_SIZE = 100;

    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private AdSlotStatDao adSlotStatDao;
    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;
    @Resource
    private SiteDao siteDao;
    @Resource
    private DspDao dspDao;
    @Resource
    private DspStatDao dspStatDao;

    @SuppressWarnings("unchecked")
    public void syncAdSlotStatData(StatDataSynchronizerParam param) {
        String syncDate = (param == null || !StringUtils.hasText(param.getSyncDate())) ? LocalDate.now()
                .format(Constants.REDIS_KEY_DATEFORMAT) : param.getSyncDate();

        String keyAdSlots = RedisKeys.keyStatAdslots(syncDate);
        Set<String> statAdSlotIds = redisTemplate.opsForSet().members(keyAdSlots);
        if (statAdSlotIds == null || statAdSlotIds.isEmpty()) {
            return;
        }

        List<AdSlotStat> adSlotStats = new ArrayList<>(statAdSlotIds.size());
        for (String adSlotId : statAdSlotIds) {
            String keyAdSlot = RedisKeys.keyStatAdSlot(adSlotId, syncDate);
            List<Object> values = redisTemplate.opsForHash().multiGet(keyAdSlot, RedisKeys.HASH_FIELDS);
            if (values == null || values.isEmpty()) {
                continue;
            }
            String impCountStr = (String) values.get(0);
            String clickCountStr = (String) values.get(1);
            String bidCountStr = (String) values.get(2);
            String winCountStr = (String) values.get(3);
            String reqCountStr = (String) values.get(4);
            String revenueStr = (String) values.get(5);
            String dspCostStr = (String) values.get(6);
            String adxRevenueStr = (String) values.get(7);

            AdSlotStat adSlotStat = new AdSlotStat();
            adSlotStat.setAdSlotId(adSlotId);
            adSlotStat.setStatDate(Integer.parseInt(syncDate));
            adSlotStat.setImpCount(NumberUtils.toLong(impCountStr));
            adSlotStat.setClickCount(NumberUtils.toLong(clickCountStr));
            adSlotStat.setBidCount(NumberUtils.toLong(bidCountStr));
            adSlotStat.setWinCount(NumberUtils.toLong(winCountStr));
            adSlotStat.setReqCount(NumberUtils.toLong(reqCountStr));
            adSlotStat.setRevenue(NumberUtils.toLong(revenueStr));
            adSlotStat.setDspCost(NumberUtils.toLong(dspCostStr));
            adSlotStat.setAdxRevenue(NumberUtils.toLong(adxRevenueStr));

            adSlotStats.add(adSlotStat);
        }
        adSlotStatDao.saveBatchOnDuplicateKeyUpdate(adSlotStats);
    }

    // 添加同步Crid统计数据的方法
    public void syncCridStatData(StatDataSynchronizerParam param) {
        // TODO: 根据实际业务需求实现crid统计数据同步
        log.warn("syncCridStatData is not implemented yet");
    }

    public void syncDspStatData(StatDataSynchronizerParam param) {
        String syncDate = (param == null || !StringUtils.hasText(param.getSyncDate())) ? LocalDate.now()
                .format(Constants.REDIS_KEY_DATEFORMAT) : param.getSyncDate();
        String keyStatDsps = RedisKeys.keyStatDsps(syncDate);
        Set<String> statDspIds = redisTemplate.opsForSet().members(keyStatDsps);
        if (statDspIds == null || statDspIds.isEmpty()) {
            return;
        }
        Map<String, Dsp> dspMap = dspDao.list(QueryWrapper.create().in(Dsp::getDspId, statDspIds))
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Dsp::getDspId, Function.identity()));

        List<DspStat> dspStats = new ArrayList<>(statDspIds.size());
        for (String dspId : statDspIds) {
            Dsp dsp = dspMap.get(dspId);
            if (dsp == null) {
                continue;
            }
            String keyStatDsp = RedisKeys.keyStatDsp(dspId, syncDate);
            List<Object> values = redisTemplate.opsForHash().multiGet(keyStatDsp, RedisKeys.HASH_FIELDS);
            if (values == null || values.isEmpty()) {
                continue;
            }
            String impCountStr = (String) values.get(0);
            String clickCountStr = (String) values.get(1);
            String bidCountStr = (String) values.get(2);
            String winCountStr = (String) values.get(3);
            String reqCountStr = (String) values.get(4);
            // String revenueStr = (String) values.get(5);
            String dspCostStr = (String) values.get(6);
            // String adxRevenueStr = (String) values.get(7);

            DspStat dspStat = new DspStat();
            dspStat.setDspId(dsp.getId());
            dspStat.setDspCode(dsp.getDspId());
            dspStat.setDspName(dsp.getName());
            dspStat.setStatDate(Integer.parseInt(syncDate));
            dspStat.setImpCount(NumberUtils.toLong(impCountStr));
            dspStat.setClkCount(NumberUtils.toLong(clickCountStr));
            dspStat.setBidCount(NumberUtils.toLong(bidCountStr));
            dspStat.setWinCount(NumberUtils.toLong(winCountStr));
            dspStat.setReqCount(NumberUtils.toLong(reqCountStr));
            dspStat.setDspCost(NumberUtils.toLong(dspCostStr));
            dspStats.add(dspStat);
        }
    }
}