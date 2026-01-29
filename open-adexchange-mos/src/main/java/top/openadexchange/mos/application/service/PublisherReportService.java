package top.openadexchange.mos.application.service;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.openadexchange.dao.AdSlotStatDao;
import top.openadexchange.domain.entity.AdSlotReportAggregate;
import top.openadexchange.domain.entity.PublisherReportAggregate;
import top.openadexchange.dto.query.ReportQueryDto;
import top.openadexchange.dto.report.AdSlotReportDto;
import top.openadexchange.dto.report.PublisherReportDto;
import top.openadexchange.mos.application.converter.PublisherReportConverter;

import java.util.List;

/**
 * 媒体报表服务
 */
@Service
@Slf4j
public class PublisherReportService {

    @Resource
    private AdSlotStatDao adSlotStatDao;

    @Resource
    private PublisherReportConverter publisherReportConverter;

    /**
     * 分页查询媒体报表
     */
    public Page<PublisherReportDto> pagePublisherReport(ReportQueryDto queryDto) {
        log.info("查询媒体报表: {}", queryDto);

        int pageNo = queryDto.getPageNo() != null ? queryDto.getPageNo() : 1;
        int pageSize = queryDto.getPageSize() != null ? queryDto.getPageSize() : 20;
        int offset = (pageNo - 1) * pageSize;

        // 查询列表（返回聚合模型）
        List<PublisherReportAggregate> aggregates = adSlotStatDao.selectPublisherReport(
                queryDto.getPublisherId(),
                queryDto.getPublisherName(),
                queryDto.getStartDate(),
                queryDto.getEndDate(),
                offset,
                pageSize
        );

        // 转换为DTO
        List<PublisherReportDto> records = publisherReportConverter.toPublisherReportDtoList(aggregates);

        // 查询总数
        Long total = adSlotStatDao.countPublisherReport(
                queryDto.getPublisherId(),
                queryDto.getPublisherName(),
                queryDto.getStartDate(),
                queryDto.getEndDate()
        );

        // 构建分页结果
        Page<PublisherReportDto> page = new Page<>();
        page.setRecords(records);
        page.setTotalRow(total);
        page.setPageNumber(pageNo);
        page.setPageSize(pageSize);
        page.setTotalPage((int) Math.ceil((double) total / pageSize));

        return page;
    }

    /**
     * 分页查询广告位报表（按媒体下钻）
     */
    public Page<AdSlotReportDto> pageAdSlotReport(ReportQueryDto queryDto) {
        log.info("查询广告位报表: {}", queryDto);

        int pageNo = queryDto.getPageNo() != null ? queryDto.getPageNo() : 1;
        int pageSize = queryDto.getPageSize() != null ? queryDto.getPageSize() : 20;
        int offset = (pageNo - 1) * pageSize;

        // 查询列表（返回聚合模型）
        List<AdSlotReportAggregate> aggregates = adSlotStatDao.selectAdSlotReport(
                queryDto.getPublisherId(),
                queryDto.getSiteId(),
                queryDto.getStartDate(),
                queryDto.getEndDate(),
                offset,
                pageSize
        );

        // 转换为DTO
        List<AdSlotReportDto> records = publisherReportConverter.toAdSlotReportDtoList(aggregates);

        // 查询总数
        Long total = adSlotStatDao.countAdSlotReport(
                queryDto.getPublisherId(),
                queryDto.getSiteId(),
                queryDto.getStartDate(),
                queryDto.getEndDate()
        );

        // 构建分页结果
        Page<AdSlotReportDto> page = new Page<>();
        page.setRecords(records);
        page.setTotalRow(total);
        page.setPageNumber(pageNo);
        page.setPageSize(pageSize);
        page.setTotalPage((int) Math.ceil((double) total / pageSize));

        return page;
    }
}
