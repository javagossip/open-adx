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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import top.openadexchange.dto.IndustryDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.IndustryQueryDto;
import top.openadexchange.model.Industry;
import top.openadexchange.mos.application.service.IndustryService;

@RestController
@RequestMapping("/v1/industries")
@Tag(name = "行业管理",
        description = "行业管理，包括行业的增删改查")
public class IndustryController {

    @Resource
    private IndustryService industryService;

    @PostMapping
    @Operation(summary = "新增行业, 创建成功返回行业 ID")
    public ApiResponse<Long> addIndustry(@RequestBody IndustryDto industryDto) {
        return ApiResponse.success(industryService.addIndustry(industryDto));
    }

    @PutMapping
    @Operation(summary = "更新行业")
    public ApiResponse<Boolean> updateIndustry(@RequestBody IndustryDto industryDto) {
        return ApiResponse.success(industryService.updateIndustry(industryDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除行业")
    public ApiResponse<Boolean> deleteIndustry(@PathVariable("id") Long id) {
        return ApiResponse.success(industryService.deleteIndustry(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取行业")
    public ApiResponse<IndustryDto> getIndustry(@PathVariable("id") Long id) {
        return ApiResponse.success(industryService.getIndustry(id));
    }

    @GetMapping
    @Operation(summary = "分页查询行业")
    public ApiResponse<Page<Industry>> pageListIndustry(IndustryQueryDto queryDto) {
        return ApiResponse.success(industryService.pageListIndustry(queryDto));
    }

    @GetMapping("/list")
    @Operation(summary = "根据父级ID查询行业列表，父ID可以为null",
            parameters = {
                    @Parameter(name = "parentId",
                            description = "父级ID")
            })
    public ApiResponse<List<Industry>> listIndustriesByParentId(@RequestParam(name = "parentId",
            required = false) Long parentId) {
        return ApiResponse.success(industryService.listIndustriesByParentId(parentId));
    }
}