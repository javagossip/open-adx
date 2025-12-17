package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.SysUser;
import top.openadexchange.mapper.UserMapper;
import top.openadexchange.dao.SysUserDao;
import org.springframework.stereotype.Service;

/**
 * 用户信息表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Service
public class SysUserDaoImpl extends ServiceImpl<UserMapper, SysUser>  implements SysUserDao{

}
