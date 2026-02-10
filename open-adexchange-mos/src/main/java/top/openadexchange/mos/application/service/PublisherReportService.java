package top.openadexchange.mos.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.Constants;
import top.openadexchange.dao.AdSlotStatDao;
import top.openadexchange.dto.query.ReportQueryDto;
import top.openadexchange.dto.report.AdSlotReportDto;
import top.openadexchange.dto.report.PublisherReportDto;
import top.openadexchange.model.AdSlotStat;

import static com.mybatisflex.core.query.QueryMethods.*;
import static top.openadexchange.model.table.AdSlotStatTableDef.*;
import static top.openadexchange.model.table.PublisherTableDef.*;
import static top.openadexchange.model.table.SiteAdPlacementTableDef.*;

/**
 * 媒体报表服务
 */
@Service
@Slf4j
public class PublisherReportService {

    @Resource
    private AdSlotStatDao adSlotStatDao;
    @Resource
    private RedisADStatService redisADStatService;

    /**
     * 分页查询媒体报表
     */
    public Page<PublisherReportDto> pagePublisherReport(ReportQueryDto queryDto) {
        log.info("查询媒体报表: {}", queryDto);
        int currentHour = Integer.parseInt(LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT));
        // 检查查询时间范围是否包含当前小时
        boolean needMergeCurrentHourData =
                queryDto.getStartDate() <= currentHour && queryDto.getEndDate() >= currentHour;

        Page<PublisherReportDto> result = adSlotStatDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .select(PUBLISHER.ID.as("publisher_id"),
                                PUBLISHER.NAME.as("publisher_name"),
                                PUBLISHER.CODE.as("publisher_code"),
                                sum(AD_SLOT_STAT.REQ_COUNT).as("req_count"),
                                sum(AD_SLOT_STAT.BID_COUNT).as("bid_count"),
                                sum(AD_SLOT_STAT.WIN_COUNT).as("win_count"),
                                sum(AD_SLOT_STAT.IMP_COUNT).as("imp_count"),
                                sum(AD_SLOT_STAT.CLICK_COUNT).as("click_count"),
                                sum(AD_SLOT_STAT.REVENUE).as("revenue"),
                                sum(AD_SLOT_STAT.ADX_REVENUE).as("adx_revenue"))
                        .from(PUBLISHER.as("t2"))
                        .leftJoin(AD_SLOT_STAT.as("t1"))
                        .on(AD_SLOT_STAT.PUBLISHER_ID.eq(PUBLISHER.ID)
                                .and(AD_SLOT_STAT.STAT_DATE.ne(currentHour))
                                .and(AD_SLOT_STAT.SITE_ID.eq(queryDto.getSiteId()))
                                .and(AD_SLOT_STAT.STAT_DATE.between(queryDto.getStartDate(), queryDto.getEndDate())))
                        .where(PUBLISHER.ID.eq(queryDto.getPublisherId())
                                .and(PUBLISHER.NAME.like(queryDto.getPublisherName())))
                        .groupBy(PUBLISHER.ID),
                PublisherReportDto.class);

        if (!needMergeCurrentHourData || !result.hasRecords()) {
            log.info("媒体报表数据为空：{}", queryDto.getStartDate());
            return result;
        }
        List<Long> publisherIds = result.getRecords()
                .stream()
                .map(PublisherReportDto::getPublisherId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, PublisherReportDto> publisherReportDtoMap =
                redisADStatService.getTodayAdSlotStatsAggregatePublisherId(publisherIds);
        result.getRecords().forEach(reportDto -> {
            PublisherReportDto publisherReportDto = publisherReportDtoMap.get(reportDto.getPublisherId());
            if (publisherReportDto != null) {
                if (reportDto.getStatDate() == null || reportDto.getStatDate() == 0) {
                    publisherReportDto.setStatDate(publisherReportDto.getStatDate());
                }
                reportDto.incrReqCount(publisherReportDto.getReqCount());
                reportDto.incrBidCount(publisherReportDto.getBidCount());
                reportDto.incrWinCount(publisherReportDto.getWinCount());
                reportDto.incrImpCount(publisherReportDto.getImpCount());
                reportDto.incrClickCount(publisherReportDto.getClickCount());
                reportDto.incrRevenue(publisherReportDto.getRevenue());
                reportDto.incrAdxRevenue(publisherReportDto.getAdxRevenue());
            }
        });
        return result;
    }

    /**
     * 分页查询广告位报表（按媒体下钻,按广告位+统计时间分组）
     */
    @Transactional
    public Page<AdSlotReportDto> pageAdSlotReport(ReportQueryDto queryDto) {
        Assert.notNull(queryDto.getPublisherId(), "publisherId不能为空");
        log.info("查询广告位报表: {}", queryDto);

        Integer currentHour = Integer.parseInt(LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT));
        // 检查查询时间范围是否包含当前小时
        boolean needMergeCurrentHourData =
                queryDto.getStartDate() <= currentHour && queryDto.getEndDate() >= currentHour;
        if (needMergeCurrentHourData) {
            Set<String> cachedHourlyStatAdSlotIds = redisADStatService.getLastHourStatAdSlotIds(currentHour.toString());
            log.info("pre init empty adslot stat: {}", cachedHourlyStatAdSlotIds);
            initAdSlotStats(cachedHourlyStatAdSlotIds, currentHour);
        }
        Page<AdSlotReportDto> result = adSlotStatDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .select(SITE_AD_PLACEMENT.CODE.as("ad_slot_id"),
                                SITE_AD_PLACEMENT.NAME.as("ad_slot_name"),
                                AD_SLOT_STAT.SITE_NAME.as("site_name"),
                                AD_SLOT_STAT.STAT_DATE.as("stat_date"),
                                sum(AD_SLOT_STAT.REQ_COUNT).as("req_count"),
                                sum(AD_SLOT_STAT.BID_COUNT).as("bid_count"),
                                sum(AD_SLOT_STAT.WIN_COUNT).as("win_count"),
                                sum(AD_SLOT_STAT.IMP_COUNT).as("imp_count"),
                                sum(AD_SLOT_STAT.CLICK_COUNT).as("click_count"),
                                sum(AD_SLOT_STAT.REVENUE).as("revenue"),
                                sum(AD_SLOT_STAT.ADX_REVENUE).as("adx_revenue"))
                        .from(SITE_AD_PLACEMENT.as("t1"))
                        .leftJoin(AD_SLOT_STAT.as("t2"))
                        .on(SITE_AD_PLACEMENT.CODE.eq(AD_SLOT_STAT.AD_SLOT_ID)
                                .and(AD_SLOT_STAT.STAT_DATE.ne(currentHour))
                                .and(AD_SLOT_STAT.STAT_DATE.between(queryDto.getStartDate(), queryDto.getEndDate())))
                        .where(AD_SLOT_STAT.PUBLISHER_ID.eq(queryDto.getPublisherId()))
                        .groupBy(SITE_AD_PLACEMENT.CODE, AD_SLOT_STAT.SITE_NAME, AD_SLOT_STAT.STAT_DATE)
                        .orderBy("imp_count DESC"),
                AdSlotReportDto.class);

        if (!needMergeCurrentHourData || !result.hasRecords()) {
            return result;
        }
        List<String> adSlotIds = result.getRecords()
                .stream()
                .filter(Objects::nonNull)
                .map(AdSlotReportDto::getAdSlotId)
                .distinct()
                .collect(Collectors.toList());
        if (adSlotIds == null || adSlotIds.isEmpty()) {
            return result;
        }
        Map<String, AdSlotReportDto> adSlotReportDtoMap =
                redisADStatService.getTodayAdSlotStatsAggregateAdSlotId(adSlotIds);
        if (adSlotReportDtoMap == null || adSlotReportDtoMap.isEmpty()) {
            return result;
        }

        Map<String, AdSlotReportDto> resultReportMap = result.getRecords()
                .stream()
                .collect(Collectors.toMap(r -> String.format("%s-%s",
                        r.getAdSlotId(),
                        r.getStatDate() == null ? currentHour : r.getStatDate()), Function.identity(), (a, b) -> a));

        adSlotReportDtoMap.forEach((adSlotId, adSlotReportDto) -> {
            AdSlotReportDto existAdSlotReport = resultReportMap.get(String.format("%s-%s", adSlotId, currentHour));
            if (existAdSlotReport != null) {
                existAdSlotReport.setSiteName(adSlotReportDto.getSiteName());
                existAdSlotReport.setSiteId(adSlotReportDto.getSiteId());
                existAdSlotReport.setAdSlotName(adSlotReportDto.getAdSlotName());
                existAdSlotReport.setStatDate(currentHour);
                existAdSlotReport.setReqCount(adSlotReportDto.getReqCount());
                existAdSlotReport.setBidCount(adSlotReportDto.getBidCount());
                existAdSlotReport.setWinCount(adSlotReportDto.getWinCount());
                existAdSlotReport.setImpCount(adSlotReportDto.getImpCount());
                existAdSlotReport.setClickCount(adSlotReportDto.getClickCount());
                existAdSlotReport.setRevenue(adSlotReportDto.getRevenue());
                existAdSlotReport.setAdxRevenue(adSlotReportDto.getAdxRevenue());
            } else {
                result.getRecords().add(adSlotReportDto);
            }
        });
        return result;
    }

    private void initAdSlotStats(Set<String> adSlotIds, Integer currentHour) {
        List<AdSlotStat> adSlotStats = adSlotIds.stream()
                .map(adSlotId -> AdSlotStat.builder().adSlotId(adSlotId).statDate(currentHour).build())
                .collect(Collectors.toList());
        adSlotStatDao.saveBatchOnDuplicateKeyUpdate(adSlotStats);
    }
}
