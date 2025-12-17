package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.UserRole;
import top.openadexchange.mapper.UserRoleMapper;
import top.openadexchange.dao.SysUserRoleDao;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Service
public class SysUserRoleDaoImpl extends ServiceImpl<UserRoleMapper, UserRole>  implements SysUserRoleDao{

}
