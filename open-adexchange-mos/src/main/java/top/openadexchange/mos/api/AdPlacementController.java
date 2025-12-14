package top.openadexchange.mos.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mybatisflex.core.paginate.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.dto.AdPlacementDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.AdPlacementQueryDto;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.mos.application.service.AdPlacementService;

@RestController
@RequestMapping("/v1/ad-placements")
@Tag(name = "广告位管理",
        description = "广告位管理，包括广告位的增删改查")
public class AdPlacementController {

    @Resource
    private AdPlacementService adPlacementService;

    @PostMapping
    @Operation(summary = "新增广告位, 创建成功返回广告位ID")
    public ApiResponse<Long> addAdPlacement(@RequestBody AdPlacementDto adPlacementDto) {
        return ApiResponse.success(adPlacementService.addAdPlacement(adPlacementDto));
    }

    @PutMapping
    @Operation(summary = "更新广告位")
    public ApiResponse<Boolean> updateAdPlacement(@RequestBody AdPlacementDto adPlacementDto) {
        return ApiResponse.success(adPlacementService.updateAdPlacement(adPlacementDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除广告位")
    public ApiResponse<Boolean> deleteAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(adPlacementService.deleteAdPlacement(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取广告位")
    public ApiResponse<AdPlacementDto> getAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(adPlacementService.getAdPlacement(id));
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用广告位")
    public ApiResponse<Boolean> enableAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(adPlacementService.enableAdPlacement(id));
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用广告位")
    public ApiResponse<Boolean> disableAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(adPlacementService.disableAdPlacement(id));
    }

    @GetMapping
    @Operation(summary = "分页查询广告位")
    public ApiResponse<Page<AdPlacement>> pageListAdPlacements(AdPlacementQueryDto queryDto) {
        return ApiResponse.success(adPlacementService.pageListAdPlacements(queryDto));
    }
}