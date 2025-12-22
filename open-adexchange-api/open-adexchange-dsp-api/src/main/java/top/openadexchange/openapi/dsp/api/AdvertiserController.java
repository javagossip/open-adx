package top.openadexchange.openapi.dsp.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.commons.dto.ApiResponse;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;
import top.openadexchange.openapi.dsp.application.service.AdvertiserService;

@RestController
@RequestMapping("/v1/advertisers")
@Tag(name = "广告主管理",
        description = "dsp广告主管理")
public class AdvertiserController {

    @Resource
    private AdvertiserService advertiserService;

    @PostMapping
    @Operation(summary = "添加广告主",
            description = "添加广告主, 广告主添加成功返回广告交易平台广告主编码")
    public ApiResponse<String> addAdvertiser(AdvertiserDto advertiserDto) {
        return ApiResponse.success(advertiserService.addAdvertiser(advertiserDto));
    }

    @PutMapping
    @Operation(summary = "更新广告主",
            description = "更新广告主信息,更新成功返回true,否则false")
    public ApiResponse<Boolean> updateAdvertiser(AdvertiserDto advertiserDto) {
        return ApiResponse.success(advertiserService.updateAdvertiser(advertiserDto));
    }

    @GetMapping("/{advertiserId}/audit-status")
    @Operation(summary = "获取广告主审核状态",
            description = "获取广告主审核状态, 审核状态包括审核中、审核通过、审核失败")
    public ApiResponse<AdvertiserAuditResultDto> getAuditStatus(@RequestParam("advertiserId") String advertiserId) {
        return ApiResponse.success(advertiserService.getAuditStatus(advertiserId));
    }

    @GetMapping("/audit-status")
    @Operation(summary = "获取所有广告主审核状态",
            description = "获取所有广告主审核状态, 审核状态包括审核中、审核通过、审核失败")
    public ApiResponse<List<AdvertiserAuditResultDto>> getAuditStatusList(@RequestParam List<String> advertiserIds) {
        return ApiResponse.success(advertiserService.getAuditStatusList(advertiserIds));
    }
}
