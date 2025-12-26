package top.openadexchange.openapi.dsp.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;
import top.openadexchange.openapi.dsp.application.service.AdvertiserService;
import top.openadexchange.openapi.dsp.commons.OpenApiResponse;

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
    public OpenApiResponse<String> addAdvertiser(AdvertiserDto advertiserDto) {
        return OpenApiResponse.success(advertiserService.addAdvertiser(advertiserDto));
    }

    @PutMapping
    @Operation(summary = "更新广告主",
            description = "更新广告主信息,更新成功返回true,否则false")
    public OpenApiResponse<Boolean> updateAdvertiser(AdvertiserDto advertiserDto) {
        return OpenApiResponse.success(advertiserService.updateAdvertiser(advertiserDto));
    }

    @GetMapping("/{advertiserId}/audit-status")
    @Operation(summary = "获取广告主审核状态",
            description = "获取广告主审核状态, 审核状态包括审核中、审核通过、审核失败")
    public OpenApiResponse<AdvertiserAuditResultDto> getAuditStatus(@PathVariable("advertiserId") String advertiserId) {
        return OpenApiResponse.success(advertiserService.getAuditStatus(advertiserId));
    }

    @GetMapping("/audit-status")
    @Operation(summary = "获取所有广告主审核状态",
            description = "获取所有广告主审核状态, 审核状态包括审核中、审核通过、审核失败")
    public OpenApiResponse<List<AdvertiserAuditResultDto>> getAuditStatusList(@RequestParam("ids") List<String> advertiserIds) {
        return OpenApiResponse.success(advertiserService.getAuditStatusList(advertiserIds));
    }
}
