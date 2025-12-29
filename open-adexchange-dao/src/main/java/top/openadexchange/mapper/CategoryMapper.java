package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.Category;

/**
 * App/Site 内容分类字典表 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-29
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
