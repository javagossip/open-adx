package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.ruoyi.system.service.ISysConfigService;

import jakarta.annotation.Resource;
import top.openadexchange.constants.SysConfigKey;
import top.openadexchange.dao.CategoryDao;
import top.openadexchange.dto.CategoryDto;
import top.openadexchange.dto.query.CategoryQueryDto;
import top.openadexchange.model.Category;
import top.openadexchange.mos.application.converter.CategoryConverter;

@Service
public class CategoryService {

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private CategoryConverter categoryConverter;
    @Resource
    private ISysConfigService sysConfigService;

    public Long addCategory(CategoryDto categoryDto) {
        Category category = categoryConverter.from(categoryDto);
        categoryDao.save(category);
        return category.getId().longValue();
    }

    public Boolean updateCategory(CategoryDto categoryDto) {
        Category category = categoryConverter.from(categoryDto);
        return categoryDao.updateById(category);
    }

    public Boolean deleteCategory(Long id) {
        return categoryDao.removeById(id);
    }

    public CategoryDto getCategory(Long id) {
        Category category = categoryDao.getById(id);
        return categoryConverter.toCategoryDto(category);
    }

    public Page<Category> pageListCategory(CategoryQueryDto queryDto) {
        return categoryDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .like(Category::getSystem, queryDto.getSystem())
                        .like(Category::getCode, queryDto.getCode())
                        .like(Category::getName, queryDto.getName())
                        .eq(Category::getStatus, queryDto.getStatus()));
    }

    public List<Category> listCategoriesByParentCode(String parentCode, String system) {
        String categorySystem = sysConfigService.selectConfigByKey(SysConfigKey.SYS_CATEGORY_SYSTEM);
        system = system == null ? categorySystem : system;

        Assert.hasText(system, "system cannot be null");
        if (parentCode == null) {
            return categoryDao.list(QueryWrapper.create()
                    .isNull(Category::getParentCode)
                    .eq(Category::getSystem, system));
        }
        return categoryDao.list(QueryWrapper.create()
                .eq(Category::getParentCode, parentCode)
                .eq(Category::getSystem, system));
    }
}