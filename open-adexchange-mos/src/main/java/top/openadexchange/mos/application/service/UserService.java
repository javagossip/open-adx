package top.openadexchange.mos.application.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.dao.SysUserDao;
import top.openadexchange.dao.SysUserRoleDao;
import top.openadexchange.dto.DspDto;
import top.openadexchange.model.SysUser;
import top.openadexchange.mos.application.factory.UserFactory;

@Service
public class UserService {

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysUserRoleDao sysUserRoleDao;

    public void createDspUser(DspDto dsp) {
    }
}
