package top.openadexchange.mos.application.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import top.openadexchange.domain.entity.AdSlotReportAggregate;
import top.openadexchange.domain.entity.PublisherReportAggregate;
import top.openadexchange.dto.report.AdSlotReportDto;
import top.openadexchange.dto.report.PublisherReportDto;

/**
 * 报表数据转换器
 */
@Component
public class PublisherReportConverter {

    /**
     * 将媒体报表聚合模型转换为DTO
     */
    public PublisherReportDto toPublisherReportDto(PublisherReportAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        return PublisherReportDto.builder()
                .publisherId(aggregate.getPublisherId())
                .publisherName(aggregate.getPublisherName())
//                .publisherCode(aggregate.getPublisherCode())
                .impCount(aggregate.getImpCount())
                .clickCount(aggregate.getClickCount())
                .clickRate(aggregate.getClickRate())
                .revenue(aggregate.getRevenue())
                .build();
    }

    /**
     * 批量转换媒体报表
     */
    public List<PublisherReportDto> toPublisherReportDtoList(List<PublisherReportAggregate> aggregates) {
        if (aggregates == null) {
            return List.of();
        }
        return aggregates.stream()
                .map(this::toPublisherReportDto)
                .collect(Collectors.toList());
    }

    /**
     * 将广告位报表聚合模型转换为DTO
     */
    public AdSlotReportDto toAdSlotReportDto(AdSlotReportAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        return AdSlotReportDto.builder()
                .adSlotId(aggregate.getAdSlotId())
                .adSlotName(aggregate.getAdSlotName())
                .siteId(aggregate.getSiteId())
                .siteName(aggregate.getSiteName())
                .publisherId(aggregate.getPublisherId())
                .publisherName(aggregate.getPublisherName())
                .impCount(aggregate.getImpCount())
                .clickCount(aggregate.getClickCount())
                .clickRate(aggregate.getClickRate())
                .revenue(aggregate.getRevenue())
                .build();
    }

    /**
     * 批量转换广告位报表
     */
    public List<AdSlotReportDto> toAdSlotReportDtoList(List<AdSlotReportAggregate> aggregates) {
        if (aggregates == null) {
            return List.of();
        }
        return aggregates.stream()
                .map(this::toAdSlotReportDto)
                .collect(Collectors.toList());
    }
}
