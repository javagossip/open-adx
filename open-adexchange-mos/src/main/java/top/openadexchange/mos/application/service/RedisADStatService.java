package top.openadexchange.mos.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.Constants;
import top.openadexchange.constants.RedisKeys;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.PublisherDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.dto.report.AdSlotReportDto;
import top.openadexchange.dto.report.DspReportDto;
import top.openadexchange.dto.report.PublisherReportDto;
import top.openadexchange.model.AdSlotStat;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Publisher;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;

@Service
@Slf4j
public class RedisADStatService {

    @Resource(name = "oaxStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;
    @Resource
    private SiteDao siteDao;
    @Resource
    private PublisherDao publisherDao;
    @Resource
    private DspDao dspDao;


    public Map<Long, PublisherReportDto> getTodayAdSlotStatsAggregatePublisherId(List<Long> publisherIds) {
        log.info("getTodayAdSlotStatsAggregatePublisherId, publisherIds: {}", publisherIds);
        List<AdSlotStat> adSlotStats = batchGetTodayAdSlotStats();
        if (adSlotStats.isEmpty()) {
            log.info("no stat adslots");
            return Collections.emptyMap();
        }
        log.info("adSlotStats: {}", adSlotStats);
        Map<Long, PublisherReportDto> publisherReportDtoMap = new ConcurrentHashMap<>();
        for (AdSlotStat adSlotStat : adSlotStats) {
            //如果publisherId不在列表中，则跳过
            if (!publisherIds.contains(adSlotStat.getPublisherId())) {
                continue;
            }
            PublisherReportDto publisherReportDto =
                    publisherReportDtoMap.computeIfAbsent(adSlotStat.getPublisherId(), k -> new PublisherReportDto());
            publisherReportDto.incrReqCount(adSlotStat.getReqCount());
            publisherReportDto.incrBidCount(adSlotStat.getBidCount());
            publisherReportDto.incrWinCount(adSlotStat.getWinCount());
            publisherReportDto.incrImpCount(adSlotStat.getImpCount());
            publisherReportDto.incrClickCount(adSlotStat.getClickCount());
            publisherReportDto.incrRevenue(adSlotStat.getRevenue());
            publisherReportDto.incrAdxRevenue(adSlotStat.getAdxRevenue());
            publisherReportDto.setPublisherId(adSlotStat.getPublisherId());
        }

        List<Publisher> publishers = publisherDao.list(QueryWrapper.create().in(Publisher::getId, publisherIds));
        if (publishers.isEmpty()) {
            return publisherReportDtoMap;
        }
        Map<Long, Publisher> publisherMap = publishers.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Publisher::getId, Function.identity(), (a, b) -> a));
        publisherReportDtoMap.forEach((k, v) -> v.setPublisherName(publisherMap.get(k).getName()));
        return publisherReportDtoMap;
    }

    public List<AdSlotStat> batchGetTodayAdSlotStats() {
        Set<String> adSlotIds = redisTemplate.opsForSet().members(RedisKeys.keyStatAdslots());
        if (adSlotIds == null || adSlotIds.isEmpty()) {
            return Collections.emptyList();
        }
        return batchGetTodayAdSlotStats(adSlotIds);
    }

    private List<AdSlotStat> batchGetTodayAdSlotStats(Collection<String> adSlotIds) {
        log.info("Get adSlotIds stat from redis: {}", adSlotIds);
        String syncDate = LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT);
        List<SiteAdPlacement> siteAdPlacements =
                siteAdPlacementDao.list(QueryWrapper.create().in(SiteAdPlacement::getCode, adSlotIds));
        if (siteAdPlacements.isEmpty()) {
            log.info("no stat adslots, statAdSlotIds: {}", adSlotIds);
            return Collections.emptyList();
        }
        List<Site> sites = siteDao.list(QueryWrapper.create()
                .in(Site::getId,
                        siteAdPlacements.stream().map(SiteAdPlacement::getSiteId).collect(Collectors.toSet())));
        if (sites.isEmpty()) {
            return Collections.emptyList();
        }
        //<siteId,Site>映射
        Map<Long, Site> siteMap =
                sites.stream().collect(Collectors.toMap(Site::getId, Function.identity(), (a, b) -> a));
        //<adSlotId,SiteAdPlacement>映射
        Map<String, SiteAdPlacement> siteAdPlacementMap = siteAdPlacements.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SiteAdPlacement::getCode, Function.identity(), (a, b) -> a));

        List<AdSlotStat> adSlotStats = new ArrayList<>(adSlotIds.size());
        for (String adSlotId : adSlotIds) {
            SiteAdPlacement siteAdPlacement = siteAdPlacementMap.get(adSlotId);
            if (siteAdPlacement == null) {
                continue;
            }
            String keyAdSlot = RedisKeys.keyStatAdSlot(adSlotId);
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
            adSlotStat.setSiteId(siteAdPlacement.getSiteId());

            Site site = siteMap.get(siteAdPlacement.getSiteId());
            adSlotStat.setPublisherId(site == null ? null : site.getPublisherId());
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
        return adSlotStats;
    }

    private List<AdSlotReportDto> batchGetTodayAdSlotReports(Collection<String> adSlotIds) {
        log.info("Get adSlotIds stat from redis: {}", adSlotIds);
        String syncDate = LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT);
        List<SiteAdPlacement> siteAdPlacements =
                siteAdPlacementDao.list(QueryWrapper.create().in(SiteAdPlacement::getCode, adSlotIds));
        if (siteAdPlacements.isEmpty()) {
            log.info("no stat adslots, statAdSlotIds: {}", adSlotIds);
            return Collections.emptyList();
        }
        List<Site> sites = siteDao.list(QueryWrapper.create()
                .in(Site::getId,
                        siteAdPlacements.stream().map(SiteAdPlacement::getSiteId).collect(Collectors.toSet())));
        if (sites.isEmpty()) {
            return Collections.emptyList();
        }
        //<siteId,Site>映射
        Map<Long, Site> siteMap =
                sites.stream().collect(Collectors.toMap(Site::getId, Function.identity(), (a, b) -> a));
        //<adSlotId,SiteAdPlacement>映射
        Map<String, SiteAdPlacement> siteAdPlacementMap = siteAdPlacements.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SiteAdPlacement::getCode, Function.identity(), (a, b) -> a));

        List<AdSlotReportDto> adSlotStats = new ArrayList<>(adSlotIds.size());
        for (String adSlotId : adSlotIds) {
            SiteAdPlacement siteAdPlacement = siteAdPlacementMap.get(adSlotId);
            if (siteAdPlacement == null) {
                continue;
            }
            String keyAdSlot = RedisKeys.keyStatAdSlot(adSlotId);
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
            //            String dspCostStr = (String) values.get(6);
            String adxRevenueStr = (String) values.get(7);

            AdSlotReportDto adSlotReportDto = new AdSlotReportDto();
            adSlotReportDto.setAdSlotId(adSlotId);
            adSlotReportDto.setStatDate(Integer.parseInt(syncDate));
            adSlotReportDto.setSiteId(siteAdPlacement.getSiteId());
            adSlotReportDto.setAdSlotName(siteAdPlacement.getName());

            Site site = siteMap.get(siteAdPlacement.getSiteId());
            adSlotReportDto.setPublisherId(site == null ? null : site.getPublisherId());
            adSlotReportDto.setSiteName(site == null ? null : site.getName());
            adSlotReportDto.setStatDate(Integer.parseInt(syncDate));
            adSlotReportDto.setImpCount(NumberUtils.toLong(impCountStr));
            adSlotReportDto.setClickCount(NumberUtils.toLong(clickCountStr));
            adSlotReportDto.setBidCount(NumberUtils.toLong(bidCountStr));
            adSlotReportDto.setWinCount(NumberUtils.toLong(winCountStr));
            adSlotReportDto.setReqCount(NumberUtils.toLong(reqCountStr));
            adSlotReportDto.setRevenue(NumberUtils.toLong(revenueStr));
            adSlotReportDto.setAdxRevenue(NumberUtils.toLong(adxRevenueStr));

            adSlotStats.add(adSlotReportDto);
        }
        return adSlotStats;
    }

    public Map<String, DspReportDto> getTodayDspStatsAggregateDspCodes(List<String> dspCodes) {
        List<DspReportDto> redisDspStats = batchGetTodayDspStats(dspCodes);
        log.info("Get dspCodes stat from redis: {}, redisDspStats: {}", dspCodes, redisDspStats);
        return redisDspStats.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DspReportDto::getDspCode, Function.identity(), (a, b) -> a));
    }

    private List<DspReportDto> batchGetTodayDspStats(List<String> dspCodes) {
        if (dspCodes == null || dspCodes.isEmpty()) {
            log.info("no stat dspCodes, statDspCodes: {}", dspCodes);
            return Collections.emptyList();
        }
        List<Dsp> dspList = dspDao.list(QueryWrapper.create().in(Dsp::getDspId, dspCodes));
        //<dspCode,Dsp>
        Map<String, Dsp> dspMap = dspList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Dsp::getDspId, Function.identity(), (a, b) -> a));
        String statDate = LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT);
        List<DspReportDto> dspStats = new ArrayList<>(dspCodes.size());
        for (String dspCode : dspCodes) {
            Dsp dsp = dspMap.get(dspCode);
            if (dsp == null) {
                continue;
            }
            List<Object> values =
                    redisTemplate.opsForHash().multiGet(RedisKeys.keyStatDsp(dspCode), RedisKeys.HASH_FIELDS);
            String reqCountStr = (String) values.get(4);
            String bidCountStr = (String) values.get(2);
            String winCountStr = (String) values.get(3);
            String impCountStr = (String) values.get(0);
            String clickCountStr = (String) values.get(1);
            String costStr = (String) values.get(6);

            DspReportDto dspStat = new DspReportDto();
            dspStat.setDspName(dsp.getName());
            dspStat.setDspId(String.valueOf(dsp.getId()));
            dspStat.setStatDate(Integer.parseInt(statDate));
            dspStat.setDspCode(dspCode);
            dspStat.setBidCount(NumberUtils.toLong(bidCountStr));
            dspStat.setReqCount(NumberUtils.toLong(reqCountStr));
            dspStat.setWinCount(NumberUtils.toLong(winCountStr));
            dspStat.setImpCount(NumberUtils.toLong(impCountStr));
            dspStat.setClkCount(NumberUtils.toLong(clickCountStr));
            dspStat.setCost(NumberUtils.toLong(costStr));
            dspStats.add(dspStat);
        }
        return dspStats;
    }

    public Map<String, AdSlotReportDto> getTodayAdSlotStatsAggregateAdSlotId(List<String> adSlotIds) {
        return batchGetTodayAdSlotReports(adSlotIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AdSlotReportDto::getAdSlotId, Function.identity(), (a, b) -> a));
    }

    public Set<String> getLastHourStatAdSlotIds(String date) {
        return redisTemplate.opsForSet().members(RedisKeys.keyStatAdslots(date));
    }

    public Set<String> getLastHourStatDspIds(String date) {
        return redisTemplate.opsForSet().members(RedisKeys.keyStatDsps(date));
    }
}
