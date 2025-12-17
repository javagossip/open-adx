package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.SysUser;

/**
 * 用户信息表 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

}
