package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.SysRole;
import top.openadexchange.mapper.RoleMapper;
import top.openadexchange.dao.SysRoleDao;
import org.springframework.stereotype.Service;

/**
 * 角色信息表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Service
public class SysRoleDaoImpl extends ServiceImpl<RoleMapper, SysRole>  implements SysRoleDao{

}
