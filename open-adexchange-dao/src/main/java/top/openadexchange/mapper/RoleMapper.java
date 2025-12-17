package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.SysRole;

/**
 * 角色信息表 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {

}
