package top.openadexchange.mos.api;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.ReportQueryDto;
import top.openadexchange.dto.report.AdSlotReportDto;
import top.openadexchange.dto.report.PublisherReportDto;
import top.openadexchange.mos.application.service.PublisherReportService;

@RestController
@RequestMapping("/v1/reports/publisher")
@Tag(name = "媒体报表管理", description = "媒体报表查询，包括媒体汇总报表和广告位明细报表")
public class PublisherReportController {

    @Resource
    private PublisherReportService publisherReportService;

    @GetMapping
    @Operation(summary = "分页查询媒体报表列表",
            description = "按媒体汇总统计曝光量、点击量、点击率和收入")
    public ApiResponse<Page<PublisherReportDto>> pagePublisherReport(ReportQueryDto queryDto) {
        return ApiResponse.success(publisherReportService.pagePublisherReport(queryDto));
    }

    @GetMapping("/adslot")
    @Operation(summary = "分页查询广告位报表列表",
            description = "按广告位汇总统计，支持按媒体ID下钻查询")
    public ApiResponse<Page<AdSlotReportDto>> pageAdSlotReport(ReportQueryDto queryDto) {
        return ApiResponse.success(publisherReportService.pageAdSlotReport(queryDto));
    }
}
