package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;

import top.openadexchange.domain.entity.AdSlotReportAggregate;
import top.openadexchange.domain.entity.PublisherReportAggregate;
import top.openadexchange.model.AdSlotStat;

import java.util.List;

/**
 * 媒体广告位数据统计 服务层。
 *
 * @author top.openadexchange
 * @since 2026-01-27
 */
public interface AdSlotStatDao extends IService<AdSlotStat> {

    void saveBatchOnDuplicateKeyUpdate(List<AdSlotStat> adSlotStats);

    /**
     * 查询媒体报表列表
     */
    List<PublisherReportAggregate> selectPublisherReport(Long publisherId, String publisherName,
                                                          Integer startDate, Integer endDate,
                                                          Integer offset, Integer limit);

    /**
     * 查询媒体报表总数
     */
    Long countPublisherReport(Long publisherId, String publisherName,
                              Integer startDate, Integer endDate);

    /**
     * 查询广告位报表列表
     */
    List<AdSlotReportAggregate> selectAdSlotReport(Long publisherId, Long siteId,
                                                    Integer startDate, Integer endDate,
                                                    Integer offset, Integer limit);

    /**
     * 查询广告位报表总数
     */
    Long countAdSlotReport(Long publisherId, Long siteId,
                           Integer startDate, Integer endDate);
}
