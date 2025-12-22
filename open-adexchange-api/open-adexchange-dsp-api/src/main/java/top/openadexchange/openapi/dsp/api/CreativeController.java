package top.openadexchange.openapi.dsp.api;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.commons.dto.ApiResponse;
import top.openadexchange.openapi.dsp.application.dto.CreativeDto;
import top.openadexchange.openapi.dsp.application.service.CreativeService;

@RestController
@RequestMapping("/v1/creatives")
@Tag(name = "DSP广告创意管理",
        description = "dsp广告创意管理,提供给dsp平台用来创意的创建以及更新")
public class CreativeController {

    @Resource
    private CreativeService creativeService;

    @PostMapping
    @Operation(summary = "创建广告创意素材")
    public ApiResponse<String> addCreative(@RequestBody CreativeDto creativeDto) {
        return ApiResponse.success(creativeService.addCreative(creativeDto));
    }

    @PutMapping
    @Operation(summary = "更新广告创意素材")
    public ApiResponse<Boolean> updateCreative(@RequestBody CreativeDto creativeDto){
        return ApiResponse.success(creativeService.updateCreative(creativeDto));
    }
}
