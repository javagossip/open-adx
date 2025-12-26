package top.openadexchange.openapi.dsp.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.openapi.dsp.application.dto.CreativeAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.CreativeDto;
import top.openadexchange.openapi.dsp.application.service.CreativeService;
import top.openadexchange.openapi.dsp.commons.OpenApiResponse;

@RestController
@RequestMapping("/v1/creatives")
@Tag(name = "DSP广告创意管理",
        description = "dsp广告创意管理,提供给dsp平台用来创意的创建以及更新")
public class CreativeController {

    @Resource
    private CreativeService creativeService;

    @PostMapping
    @Operation(summary = "创建广告创意素材")
    public OpenApiResponse<String> addCreative(@RequestBody CreativeDto creativeDto) {
        return OpenApiResponse.success(creativeService.addCreative(creativeDto));
    }

    @PutMapping
    @Operation(summary = "更新广告创意素材")
    public OpenApiResponse<Boolean> updateCreative(@RequestBody CreativeDto creativeDto) {
        return OpenApiResponse.success(creativeService.updateCreative(creativeDto));
    }

    @GetMapping("/{creativeId}/audit-status")
    @Operation(summary = "获取单个广告素材审核状态")
    public OpenApiResponse<CreativeAuditResultDto> getCreativeAuditStatus(@PathVariable String creativeId) {
        return OpenApiResponse.success(creativeService.getCreativeAuditStatus(creativeId));
    }

    @GetMapping("/audit-status")
    @Operation(summary = "批量获取广告素材审核状态")
    public OpenApiResponse<List<CreativeAuditResultDto>> getCreativeAuditStatusList(@RequestParam("creativeIds") List<String> creativeIds) {
        return OpenApiResponse.success(creativeService.getCreativeAuditStatusList(creativeIds));
    }
}
