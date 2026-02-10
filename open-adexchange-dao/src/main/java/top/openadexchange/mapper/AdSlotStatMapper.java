package top.openadexchange.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mybatisflex.core.BaseMapper;

import top.openadexchange.domain.entity.AdSlotReportAggregate;
import top.openadexchange.domain.entity.PublisherReportAggregate;
import top.openadexchange.model.AdSlotStat;

/**
 * 媒体广告位数据统计 映射层。
 *
 * @author top.openadexchange
 * @since 2026-01-27
 */
@Mapper
public interface AdSlotStatMapper extends BaseMapper<AdSlotStat> {

    @Insert("""
            <script>
            INSERT INTO ad_slot_stat (
                ad_slot_id, 
                ad_slot_name,
                publisher_id, 
                publisher_name,
                site_id, 
                site_name,
                stat_date,
                req_count,
                bid_count,
                win_count, 
                imp_count, 
                click_count,
                dsp_cost,
                revenue,
                adx_revenue
            ) VALUES 
            <foreach collection='list' item='item' separator=','>
                (#{item.adSlotId},#{item.adSlotName}, #{item.publisherId}, #{item.publisherName},#{item.siteId}, #{item.siteName},#{item.statDate}, #{item.reqCount},
                #{item.bidCount},#{item.winCount},#{item.impCount}, #{item.clickCount},#{item.dspCost},
                #{item.revenue},#{item.adxRevenue}
                )
            </foreach>
            ON DUPLICATE KEY UPDATE 
                req_count=VALUES(req_count), 
                bid_count = VALUES(bid_count), 
                win_count = VALUES(win_count), 
                imp_count = VALUES(imp_count), 
                click_count = VALUES(click_count), 
                dsp_cost = VALUES(dsp_cost), 
                revenue = VALUES(revenue), 
                adx_revenue = VALUES(adx_revenue)
            </script>
            """)
    void saveBatchOnDuplicateKeyUpdate(@Param("list") List<AdSlotStat> adSlotStats);

    /**
     * 查询媒体报表列表（按媒体聚合）
     */
    List<PublisherReportAggregate> selectPublisherReport(@Param("publisherId") Long publisherId,
            @Param("publisherName") String publisherName,
            @Param("startDate") Integer startDate,
            @Param("endDate") Integer endDate,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 查询媒体报表总数
     */
    Long countPublisherReport(@Param("publisherId") Long publisherId,
            @Param("publisherName") String publisherName,
            @Param("startDate") Integer startDate,
            @Param("endDate") Integer endDate);

    /**
     * 查询广告位报表列表（按广告位聚合）
     */
    List<AdSlotReportAggregate> selectAdSlotReport(@Param("publisherId") Long publisherId,
            @Param("siteId") Long siteId,
            @Param("startDate") Integer startDate,
            @Param("endDate") Integer endDate,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 查询广告位报表总数
     */
    Long countAdSlotReport(@Param("publisherId") Long publisherId,
            @Param("siteId") Long siteId,
            @Param("startDate") Integer startDate,
            @Param("endDate") Integer endDate);
}
