package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.UserRole;

/**
 * 用户和角色关联表 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
