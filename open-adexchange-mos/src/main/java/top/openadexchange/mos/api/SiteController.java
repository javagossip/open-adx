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
import top.openadexchange.dto.SiteDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.SiteQueryDto;
import top.openadexchange.model.Site;
import top.openadexchange.mos.application.service.SiteService;

@RestController
@RequestMapping("/v1/sites")
@Tag(name = "站点管理",
        description = "站点管理，包括站点的增删改查")
public class SiteController {

    @Resource
    private SiteService siteService;

    @PostMapping
    @Operation(summary = "新增站点, 创建成功返回站点ID")
    public ApiResponse<Long> addSite(@RequestBody SiteDto siteDto) {
        return ApiResponse.success(siteService.addSite(siteDto));
    }

    @PutMapping
    @Operation(summary = "更新站点")
    public ApiResponse<Boolean> updateSite(@RequestBody SiteDto siteDto) {
        return ApiResponse.success(siteService.updateSite(siteDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除站点")
    public ApiResponse<Boolean> deleteSite(@PathVariable("id") Long id) {
        return ApiResponse.success(siteService.deleteSite(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取站点")
    public ApiResponse<SiteDto> getSite(@PathVariable("id") Long id) {
        return ApiResponse.success(siteService.getSite(id));
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "启用站点")
    public ApiResponse<Boolean> enableSite(@PathVariable("id") Long id) {
        return ApiResponse.success(siteService.enableSite(id));
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用站点")
    public ApiResponse<Boolean> disableSite(@PathVariable("id") Long id) {
        return ApiResponse.success(siteService.disableSite(id));
    }

    @GetMapping
    @Operation(summary = "分页查询站点")
    public ApiResponse<Page<Site>> pageListSites(SiteQueryDto siteQueryDto) {
        return ApiResponse.success(siteService.pageListSites(siteQueryDto));
    }
}