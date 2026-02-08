package top.openadexchange.mos.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mybatisflex.core.paginate.Page;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.DspReportQueryDto;
import top.openadexchange.dto.report.DspReportDto;
import top.openadexchange.mos.application.service.DspReportService;

@RestController
@RequestMapping("/v1/reports/dsp")
public class DspReportController {

    @Resource
    private DspReportService dspReportService;

    @GetMapping
    @Operation(summary = "分页查询DSP报表列表",
            description = "按DSP汇总统计曝光量、点击量、点击率和收入")
    public ApiResponse<Page<DspReportDto>> pageDspReports(DspReportQueryDto queryDto) {
        Page<DspReportDto> page = dspReportService.pageDspReports(queryDto);
        return ApiResponse.success(page);
    }
}
