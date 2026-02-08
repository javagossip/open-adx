package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.AdSlotStatDao;
import top.openadexchange.domain.entity.AdSlotReportAggregate;
import top.openadexchange.domain.entity.PublisherReportAggregate;
import top.openadexchange.mapper.AdSlotStatMapper;
import top.openadexchange.model.AdSlotStat;

/**
 * 媒体广告位数据统计 服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-27
 */
@Service
public class AdSlotStatDaoImpl extends ServiceImpl<AdSlotStatMapper, AdSlotStat> implements AdSlotStatDao {

    @Override
    public void saveBatchOnDuplicateKeyUpdate(List<AdSlotStat> adSlotStats) {
        getMapper().saveBatchOnDuplicateKeyUpdate(adSlotStats);
    }

    @Override
    public List<PublisherReportAggregate> selectPublisherReport(Long publisherId, String publisherName,
                                                                 Integer startDate, Integer endDate,
                                                                 Integer offset, Integer limit) {
        return getMapper().selectPublisherReport(publisherId, publisherName, startDate, endDate, offset, limit);
    }

    @Override
    public Long countPublisherReport(Long publisherId, String publisherName,
                                     Integer startDate, Integer endDate) {
        return getMapper().countPublisherReport(publisherId, publisherName, startDate, endDate);
    }

    @Override
    public List<AdSlotReportAggregate> selectAdSlotReport(Long publisherId, Long siteId,
                                                           Integer startDate, Integer endDate,
                                                           Integer offset, Integer limit) {
        return getMapper().selectAdSlotReport(publisherId, siteId, startDate, endDate, offset, limit);
    }

    @Override
    public Long countAdSlotReport(Long publisherId, Long siteId,
                                  Integer startDate, Integer endDate) {
        return getMapper().countAdSlotReport(publisherId, siteId, startDate, endDate);
    }
}
