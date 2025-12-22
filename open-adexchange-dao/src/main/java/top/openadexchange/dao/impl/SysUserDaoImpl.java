package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.SysUserDao;
import top.openadexchange.mapper.UserMapper;
import top.openadexchange.model.User;

/**
 * 用户信息表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Service
public class SysUserDaoImpl extends ServiceImpl<UserMapper, User> implements SysUserDao {

}
