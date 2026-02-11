package top.openadexchange.job.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.Constants;
import top.openadexchange.constants.RedisKeys;
import top.openadexchange.dao.AdSlotStatDao;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspStatDao;
import top.openadexchange.dao.PublisherDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.job.handler.StatDataSynchronizer.StatDataSynchronizerParam;
import top.openadexchange.model.AdSlotStat;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspStat;
import top.openadexchange.model.Publisher;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;

@Service
@Slf4j
public class StatDataSynchronizerService {

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
    @Resource
    private PublisherDao publisherDao;

    @SuppressWarnings("unchecked")
    public void syncAdSlotStatData(StatDataSynchronizerParam param) {
        String syncDate = (param == null || !StringUtils.hasText(param.getSyncDate())) ? LocalDateTime.now()
                .minusHours(1L)
                .format(Constants.REDIS_KEY_DATEFORMAT) : param.getSyncDate();

        String keyAdSlots = RedisKeys.keyStatAdslots(syncDate);
        Set<String> statAdSlotIds = redisTemplate.opsForSet().members(keyAdSlots);
        if (statAdSlotIds == null || statAdSlotIds.isEmpty()) {
            return;
        }

        List<SiteAdPlacement> siteAdPlacements =
                siteAdPlacementDao.list(QueryWrapper.create().in(SiteAdPlacement::getCode, statAdSlotIds));
        if (siteAdPlacements.isEmpty()) {
            log.info("no stat adslots, statAdSlotIds: {}", statAdSlotIds);
            return;
        }
        List<Site> sites = siteDao.list(QueryWrapper.create()
                .in(Site::getId,
                        siteAdPlacements.stream().map(SiteAdPlacement::getSiteId).collect(Collectors.toSet())));
        if (sites.isEmpty()) {
            log.warn("no sites found");
            return;
        }
        List<Publisher> publishers = publisherDao.list(QueryWrapper.create()
                .in(Publisher::getId, sites.stream().map(Site::getPublisherId).collect(Collectors.toSet())));
        if (publishers.isEmpty()) {
            log.warn("no publishers found");
            return;
        }
        Map<Long, Site> siteMap =
                sites.stream().collect(Collectors.toMap(Site::getId, Function.identity(), (a, b) -> a));
        Map<String, SiteAdPlacement> siteAdPlacementMap = siteAdPlacements.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SiteAdPlacement::getCode, Function.identity(), (a, b) -> a));
        Map<Long, Publisher> publisherMap = publishers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Publisher::getId, Function.identity(), (a, b) -> a));

        List<AdSlotStat> adSlotStats = new ArrayList<>(statAdSlotIds.size());
        for (String adSlotId : statAdSlotIds) {
            SiteAdPlacement siteAdPlacement = siteAdPlacementMap.get(adSlotId);
            if (siteAdPlacement == null) {
                log.warn("site ad placement not exists, adSlotId: {}", adSlotId);
                continue;
            }
            Site site = siteMap.get(siteAdPlacement.getSiteId());
            if (site == null) {
                log.warn("site not exists, siteId: {}", siteAdPlacement.getSiteId());
                continue;
            }
            Publisher publisher = publisherMap.get(site.getPublisherId());
            if (publisher == null) {
                log.warn("publisher not exists, publisherId: {}", site.getPublisherId());
                continue;
            }
            String keyAdSlot = RedisKeys.keyStatAdSlot(adSlotId, syncDate);
            /**
             * public static final List HASH_FIELDS = Arrays.asList(
             *             HASH_FIELD_IMP,
             *             HASH_FIELD_CLK,
             *             HASH_FIELD_BID,
             *             HASH_FIELD_WIN,
             *             HASH_FIELD_REQ,
             *             HASH_FIELD_REVENUE,
             *             HASH_FIELD_DSP_COST,
             *             HASH_FIELD_ADX_REVENUE
             *             );
             */
            List<Object> values = multiHashGet(keyAdSlot, RedisKeys.HASH_FIELDS);
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
            adSlotStat.setAdSlotName(siteAdPlacement.getName());
            adSlotStat.setSiteId(siteAdPlacement.getSiteId());
            adSlotStat.setSiteName(site.getName());

            adSlotStat.setPublisherId(site.getPublisherId());
            adSlotStat.setPublisherName(publisher.getName());
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
        log.info("syncAdSlotStatData start, statAdSlotStats: {}", adSlotStats);
        adSlotStatDao.saveBatchOnDuplicateKeyUpdate(adSlotStats);
    }

    // 添加同步Crid统计数据的方法
    public void syncCridStatData(StatDataSynchronizerParam param) {
        // TODO: 根据实际业务需求实现crid统计数据同步
        log.warn("syncCridStatData is not implemented yet");
    }

    public void syncDspStatData(StatDataSynchronizerParam param) {
        String syncDate = (param == null || !StringUtils.hasText(param.getSyncDate())) ? LocalDateTime.now()
                .minusHours(1L)
                .format(Constants.REDIS_KEY_DATEFORMAT) : param.getSyncDate();
        String keyStatDsps = RedisKeys.keyStatDsps(syncDate);
        Set<String> statDspIds = redisTemplate.opsForSet().members(keyStatDsps);
        if (statDspIds == null || statDspIds.isEmpty()) {
            return;
        }
        log.info("syncDspStatData start, statDspIds: {}", statDspIds);
        Map<String, Dsp> dspMap = dspDao.list(QueryWrapper.create().in(Dsp::getDspId, statDspIds))
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Dsp::getDspId, Function.identity(), (a, b) -> a));

        List<DspStat> dspStats = new ArrayList<>(statDspIds.size());
        for (String dspId : statDspIds) {
            Dsp dsp = dspMap.get(dspId);
            if (dsp == null) {
                continue;
            }
            String keyStatDsp = RedisKeys.keyStatDsp(dspId, syncDate);
            List<Object> values = multiHashGet(keyStatDsp, RedisKeys.HASH_FIELDS);
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
            dspStat.setCost(NumberUtils.toLong(dspCostStr));
            dspStats.add(dspStat);
        }
        dspStatDao.saveBatchOnDuplicateKeyUpdate(dspStats);
    }

    public List<Object> multiHashGet(String key, List<Object> fields) {
        return redisTemplate.opsForHash().multiGet(key, fields);
    }
}