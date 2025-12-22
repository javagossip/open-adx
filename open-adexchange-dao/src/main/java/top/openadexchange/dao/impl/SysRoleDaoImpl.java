package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.SysRoleDao;
import top.openadexchange.mapper.RoleMapper;
import top.openadexchange.model.Role;

/**
 * 角色信息表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Service
public class SysRoleDaoImpl extends ServiceImpl<RoleMapper, Role> implements SysRoleDao {

}
