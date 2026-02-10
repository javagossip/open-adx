package top.openadexchange.mos.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
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

import static com.mybatisflex.core.query.QueryMethods.*;
import static top.openadexchange.model.table.AdSlotStatTableDef.*;
import static top.openadexchange.model.table.PublisherTableDef.*;
import static top.openadexchange.model.table.SiteAdPlacementTableDef.*;
import static top.openadexchange.model.table.SiteTableDef.*;

/**
 * 媒体报表服务
 */
@Service
@Slf4j
public class PublisherReportService {

    @Resource
    private AdSlotStatDao adSlotStatDao;
    @Resource
    private RedisAdStatService redisAdStatService;

    /**
     * 分页查询媒体报表
     */
    public Page<PublisherReportDto> pagePublisherReport(ReportQueryDto queryDto) {
        log.info("查询媒体报表: {}", queryDto);
        int currentHour = Integer.parseInt(LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT));
        Page<PublisherReportDto> result = adSlotStatDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .select(PUBLISHER.ID.as("publisher_id"),
                                PUBLISHER.NAME.as("publisher_name"),
                                PUBLISHER.CODE.as("publisher_code"),
                                //                                AD_SLOT_STAT.STAT_DATE.as("stat_date"),
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
                                .and(AD_SLOT_STAT.PUBLISHER_ID.eq(queryDto.getPublisherId()))
                                .and(AD_SLOT_STAT.SITE_ID.eq(queryDto.getSiteId()))
                                .and(PUBLISHER.NAME.like(queryDto.getPublisherName()))
                                .and(AD_SLOT_STAT.STAT_DATE.between(queryDto.getStartDate(), queryDto.getEndDate())))
                        .groupBy(PUBLISHER.ID),
                PublisherReportDto.class);

        // 检查查询时间范围是否包含当前小时
        boolean needMergeCurrentHourData =
                queryDto.getStartDate() <= currentHour && queryDto.getEndDate() >= currentHour;

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
                redisAdStatService.getTodayAdSlotStatsAggregatePublisherId(publisherIds);
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
    public Page<AdSlotReportDto> pageAdSlotReport(ReportQueryDto queryDto) {
        Assert.notNull(queryDto.getPublisherId(), "publisherId不能为空");
        log.info("查询广告位报表: {}", queryDto);

        Integer currentHour = Integer.parseInt(LocalDateTime.now().format(Constants.REDIS_KEY_DATEFORMAT));
        Page<AdSlotReportDto> result = adSlotStatDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .select(SITE_AD_PLACEMENT.CODE.as("ad_slot_id"),
                                SITE_AD_PLACEMENT.NAME.as("ad_slot_name"),
                                SITE.PUBLISHER_ID.as("publisher_id"),
                                SITE_AD_PLACEMENT.SITE_ID.as("site_id"),
                                SITE.NAME.as("site_name"),
                                AD_SLOT_STAT.STAT_DATE.as("stat_date"),
                                sum(AD_SLOT_STAT.REQ_COUNT).as("req_count"),
                                sum(AD_SLOT_STAT.BID_COUNT).as("bid_count"),
                                sum(AD_SLOT_STAT.WIN_COUNT).as("win_count"),
                                sum(AD_SLOT_STAT.IMP_COUNT).as("imp_count"),
                                sum(AD_SLOT_STAT.CLICK_COUNT).as("click_count"),
                                sum(AD_SLOT_STAT.REVENUE).as("revenue"),
                                sum(AD_SLOT_STAT.ADX_REVENUE).as("adx_revenue"))
                        .from(SITE_AD_PLACEMENT)
                        .join(SITE)
                        .on(SITE_AD_PLACEMENT.SITE_ID.eq(SITE.ID).and(SITE.PUBLISHER_ID.eq(queryDto.getPublisherId())))
                        .leftJoin(AD_SLOT_STAT)
                        .on(SITE_AD_PLACEMENT.SITE_ID.eq(AD_SLOT_STAT.SITE_ID)
                                .and(SITE_AD_PLACEMENT.CODE.eq(AD_SLOT_STAT.AD_SLOT_ID))
                                .and(AD_SLOT_STAT.PUBLISHER_ID.eq(queryDto.getPublisherId()))
                                .and(AD_SLOT_STAT.STAT_DATE.ne(currentHour))
                                .and(AD_SLOT_STAT.STAT_DATE.between(queryDto.getStartDate(), queryDto.getEndDate())))
                        .where(SITE_AD_PLACEMENT.SITE_ID.eq(queryDto.getSiteId()))
                        .groupBy(SITE_AD_PLACEMENT.CODE,
                                SITE_AD_PLACEMENT.NAME,
                                SITE.PUBLISHER_ID,
                                SITE_AD_PLACEMENT.SITE_ID,
                                SITE.NAME,
                                AD_SLOT_STAT.STAT_DATE)
                        .orderBy("imp_count DESC"),
                AdSlotReportDto.class);

        // 检查查询时间范围是否包含当前小时
        boolean needMergeCurrentHourData =
                queryDto.getStartDate() <= currentHour && queryDto.getEndDate() >= currentHour;

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
                redisAdStatService.getTodayAdSlotStatsAggregateAdSlotId(adSlotIds);
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
}
