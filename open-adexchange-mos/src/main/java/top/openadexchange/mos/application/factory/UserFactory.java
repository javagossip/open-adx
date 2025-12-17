package top.openadexchange.mos.application.factory;


import org.springframework.stereotype.Component;

import com.mybatisflex.core.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;

import jakarta.annotation.Resource;
import top.openadexchange.dao.SysRoleDao;
import top.openadexchange.dto.DspDto;
import top.openadexchange.dto.PublisherDto;
import top.openadexchange.model.Role;
import top.openadexchange.model.enums.UserStatus;

@Component
public class UserFactory {

    @Resource
    private SysRoleDao sysRoleDao;

    //创建dsp成功自动创建用户，然后绑定dsp角色
    public SysUser forDsp(DspDto dsp) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(dsp.getName());
        sysUser.setPassword(SecurityUtils.encryptPassword(dsp.getPassword()));
        sysUser.setEmail(dsp.getContactEmail());
        sysUser.setPhonenumber(dsp.getContactPhone());
        sysUser.setStatus(UserStatus.ACTIVE.getValue());
        sysUser.setNickName(dsp.getName());

        Role role = sysRoleDao.getOne(QueryWrapper.create().eq(Role::getRoleKey, "dsp"));
        sysUser.setRoleIds(new Long[]{role.getRoleId()});
        return sysUser;
    }

    //创建媒体成功自动创建用户，然后绑定publisher角色
    public SysUser forPublisher(PublisherDto publisher) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(publisher.getName());
        sysUser.setNickName(publisher.getName());
        sysUser.setPassword(SecurityUtils.encryptPassword(publisher.getPassword()));
        sysUser.setEmail(publisher.getContactEmail());
        sysUser.setPhonenumber(publisher.getContactPhone());
        sysUser.setStatus(UserStatus.ACTIVE.getValue());

        Role role = sysRoleDao.getOne(QueryWrapper.create().eq(Role::getRoleKey, "publisher"));
        sysUser.setRoleIds(new Long[]{role.getRoleId()});
        return sysUser;
    }
}
