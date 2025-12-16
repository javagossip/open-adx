package top.openadexchange.mos.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mybatisflex.core.paginate.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.dto.SiteAdPlacementDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.SiteAdPlacementQueryDto;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.mos.application.service.SiteAdPlacementService;

@RestController
@RequestMapping("/v1/site-ad-placements")
@Tag(name = "媒体广告位管理",
        description = "媒体广告位管理，包括媒体广告位的增删改查")
public class SiteAdPlacementController {

    @Resource
    private SiteAdPlacementService siteAdPlacementService;

    @PostMapping
    @Operation(summary = "新增媒体广告位, 创建成功返回媒体广告位ID")
    public ApiResponse<Long> addSiteAdPlacement(@RequestBody SiteAdPlacementDto siteAdPlacementDto) {
        return ApiResponse.success(siteAdPlacementService.addSiteAdPlacement(siteAdPlacementDto));
    }

    @PutMapping
    @Operation(summary = "更新媒体广告位")
    public ApiResponse<Boolean> updateSiteAdPlacement(@RequestBody SiteAdPlacementDto siteAdPlacementDto) {
        return ApiResponse.success(siteAdPlacementService.updateSiteAdPlacement(siteAdPlacementDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除媒体广告位")
    public ApiResponse<Boolean> deleteSiteAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(siteAdPlacementService.deleteSiteAdPlacement(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取媒体广告位")
    public ApiResponse<SiteAdPlacementDto> getSiteAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(siteAdPlacementService.getSiteAdPlacement(id));
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用媒体广告位")
    public ApiResponse<Boolean> enableSiteAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(siteAdPlacementService.enableSiteAdPlacement(id));
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用媒体广告位")
    public ApiResponse<Boolean> disableSiteAdPlacement(@PathVariable("id") Long id) {
        return ApiResponse.success(siteAdPlacementService.disableSiteAdPlacement(id));
    }

    @GetMapping
    @Operation(summary = "分页查询媒体广告位")
    public ApiResponse<Page<SiteAdPlacement>> pageListSiteAdPlacements(SiteAdPlacementQueryDto queryDto) {
        return ApiResponse.success(siteAdPlacementService.pageListSiteAdPlacements(queryDto));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索媒体广告位, 按媒体广告位名称模糊搜索")
    public ApiResponse<List<SiteAdPlacement>> searchSiteAdPlacements(@RequestParam(name = "searchKey",
                    required = false) String searchKey,
            @RequestParam(name = "size",
                    defaultValue = "20",
                    required = false) Integer size) {
        return ApiResponse.success(siteAdPlacementService.searchSiteAdPlacements(searchKey, size));
    }
}