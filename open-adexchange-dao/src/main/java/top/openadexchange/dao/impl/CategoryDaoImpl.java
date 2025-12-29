package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.Category;
import top.openadexchange.mapper.CategoryMapper;
import top.openadexchange.dao.CategoryDao;
import org.springframework.stereotype.Service;

/**
 * App/Site 内容分类字典表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-29
 */
@Service
public class CategoryDaoImpl extends ServiceImpl<CategoryMapper, Category>  implements CategoryDao{

}
