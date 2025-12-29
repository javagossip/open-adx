package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;
import top.openadexchange.dto.CategoryDto;
import top.openadexchange.model.Category;

@Component
public class CategoryConverter {

    public Category from(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setSystem(categoryDto.getSystem());
        category.setCode(categoryDto.getCode());
        category.setName(categoryDto.getName());
        category.setParentCode(categoryDto.getParentCode());
        category.setLevel(categoryDto.getLevel());
        category.setStatus(categoryDto.getStatus());
        category.setDescription(categoryDto.getDescription());

        return category;
    }

    public CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setSystem(category.getSystem());
        categoryDto.setCode(category.getCode());
        categoryDto.setName(category.getName());
        categoryDto.setParentCode(category.getParentCode());
        categoryDto.setLevel(category.getLevel());
        categoryDto.setStatus(category.getStatus());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setCreatedAt(category.getCreatedAt());
        categoryDto.setUpdatedAt(category.getUpdatedAt());

        return categoryDto;
    }
}