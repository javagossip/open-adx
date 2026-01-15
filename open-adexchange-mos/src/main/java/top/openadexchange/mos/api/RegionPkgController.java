package top.openadexchange.mos.api;

import com.mybatisflex.core.paginate.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import top.openadexchange.dto.RegionPkgDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.RegionPkgQueryDto;
import top.openadexchange.model.RegionPkg;
import top.openadexchange.mos.application.service.RegionPkgService;

@RestController
@RequestMapping("/v1/region-pkgs")
@Tag(name = "地域包管理",
        description = "地域包管理相关接口")
public class RegionPkgController {

    @Resource
    private RegionPkgService regionPkgService;

    @PostMapping
    @Operation(summary = "新增地域包",
            description = "新增地域包并关联地区编码列表")
    public ApiResponse<Integer> addRegionPkg(@RequestBody RegionPkgDto regionPkgDto) {
        return ApiResponse.success(regionPkgService.addRegionPkg(regionPkgDto));
    }

    @PutMapping
    @Operation(summary = "更新地域包",
            description = "更新地域包及关联的地区编码列表")
    public ApiResponse<Boolean> updateRegionPkg(@RequestBody RegionPkgDto regionPkgDto) {
        return ApiResponse.success(regionPkgService.updateRegionPkg(regionPkgDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除地域包",
            description = "删除地域包及其关联的地区编码")
    public ApiResponse<Boolean> deleteRegionPkg(@PathVariable("id") Integer id) {
        return ApiResponse.success(regionPkgService.deleteRegionPkg(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取地域包详情",
            description = "根据ID获取地域包详情及关联的地区编码列表")
    public ApiResponse<RegionPkgDto> getRegionPkg(@PathVariable("id") Integer id) {
        return ApiResponse.success(regionPkgService.getRegionPkg(id));
    }

    @GetMapping
    @Operation(summary = "分页查询地域包",
            description = "分页查询地域包信息")
    public ApiResponse<Page<RegionPkg>> getRegionPkgPage(RegionPkgQueryDto queryDto) {
        return ApiResponse.success(regionPkgService.pageListRegionPkg(queryDto));
    }
}
