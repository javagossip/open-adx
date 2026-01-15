package top.openadexchange.mos.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.commons.dto.ApiResponse;
import top.openadexchange.dto.FloorPriceDto;
import top.openadexchange.dto.FloorPriceQueryDto;
import top.openadexchange.mos.application.service.FloorPriceService;

@RestController
@RequestMapping("/v1/floor-prices")
@Tag(name = "底价管理", description = "底价管理相关接口")
public class FloorPriceController {

    @Resource
    private FloorPriceService floorPriceService;

    @PostMapping
    @Operation(summary = "新增底价", description = "新增底价信息")
    public ApiResponse<Integer> addFloorPrice(@RequestBody FloorPriceDto floorPriceDto) {
        return ApiResponse.success(floorPriceService.addFloorPrice(floorPriceDto));
    }

    @PutMapping
    @Operation(summary = "更新底价", description = "根据媒体广告位id更新底价信息")
    public ApiResponse<Boolean> updateFloorPrice(@RequestBody FloorPriceDto floorPriceDto) {
        return ApiResponse.success(floorPriceService.updateFloorPrice(floorPriceDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除底价", description = "根据ID删除底价信息")
    public ApiResponse<Boolean> deleteFloorPrice(@PathVariable("id") Integer id) {
        return ApiResponse.success(floorPriceService.deleteFloorPrice(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取底价详情", description = "根据ID获取底价详情")
    public ApiResponse<FloorPriceDto> getFloorPrice(@PathVariable("id") Integer id) {
        return ApiResponse.success(floorPriceService.getFloorPriceById(id));
    }

    @GetMapping
    @Operation(summary = "分页查询底价", description = "分页查询底价信息")
    public ApiResponse<com.mybatisflex.core.paginate.Page<FloorPriceDto>> getFloorPricePage(FloorPriceQueryDto queryDto) {
        return ApiResponse.success(floorPriceService.getFloorPricePage(queryDto));
    }

}
