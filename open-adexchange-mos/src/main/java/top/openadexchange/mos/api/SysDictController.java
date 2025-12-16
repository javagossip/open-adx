package top.openadexchange.mos.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.dto.OptionDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.model.District;
import top.openadexchange.mos.application.service.SysDictService;

@RestController
@RequestMapping("/v1/sys-dicts")
@Tag(name = "系统字典管理",
        description = "系统字典管理，包括系统字典的增删改查")
public class SysDictController {

    @Resource
    private SysDictService sysDictService;

    @GetMapping("/options")
    @Operation(summary = "获取字典选项, 主要用在下拉框")
    public ApiResponse<List<OptionDto>> getOptionsByDictType(@RequestParam("type") String dictType) {
        return ApiResponse.success(sysDictService.getOptionsByType(dictType));
    }

    @GetMapping("/districts")
    @Operation(summary = "获取行政区划")
    public ApiResponse<List<District>> districts(@RequestParam(name = "parentId",
            required = false,
            defaultValue = "0") Integer parentId) {
        return ApiResponse.success(sysDictService.getDistrictsByParentId(parentId));
    }

    @GetMapping("/countries")
    @Operation(summary = "获取国家列表, 支持模糊搜索")
    public ApiResponse<List<OptionDto>> getCountries(@RequestParam(name = "searchKey",
            required = false) String searchKey) {
        return ApiResponse.success(sysDictService.getCountries(searchKey));
    }
}
