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
import top.openadexchange.dto.CategoryDto;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.dto.query.CategoryQueryDto;
import top.openadexchange.model.Category;
import top.openadexchange.mos.application.service.CategoryService;

@RestController
@RequestMapping("/v1/categories")
@Tag(name = "分类管理",
        description = "分类管理，包括分类的增删改查")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "新增分类, 创建成功返回分类 ID")
    public ApiResponse<Long> addCategory(@RequestBody CategoryDto categoryDto) {
        return ApiResponse.success(categoryService.addCategory(categoryDto));
    }

    @PutMapping
    @Operation(summary = "更新分类")
    public ApiResponse<Boolean> updateCategory(@RequestBody CategoryDto categoryDto) {
        return ApiResponse.success(categoryService.updateCategory(categoryDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public ApiResponse<Boolean> deleteCategory(@PathVariable("id") Long id) {
        return ApiResponse.success(categoryService.deleteCategory(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类")
    public ApiResponse<CategoryDto> getCategory(@PathVariable("id") Long id) {
        return ApiResponse.success(categoryService.getCategory(id));
    }

    @GetMapping
    @Operation(summary = "分页查询分类")
    public ApiResponse<Page<Category>> pageListCategory(CategoryQueryDto queryDto) {
        return ApiResponse.success(categoryService.pageListCategory(queryDto));
    }

    @GetMapping("/list")
    @Operation(summary = "根据父级分类编码和分类体系查询分类列表，父级分类编码可以为空",
            parameters = {
                    @Parameter(name = "parentCode",
                            description = "父级分类编码"),
                    @Parameter(name = "system",
                            description = "分类体系",
                            required = true)
            })
    public ApiResponse<List<Category>> listCategoriesByParentId(@RequestParam(name = "parentCode",
            required = false) String parentCode, @RequestParam(name = "system") String system) {
        return ApiResponse.success(categoryService.listCategoriesByParentCode(parentCode,system));
    }
}