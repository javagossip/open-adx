package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.Assert;

import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspSiteAdPlacementDao;
import top.openadexchange.dao.DspTargetingDao;
import top.openadexchange.dto.DspDto;
import top.openadexchange.dto.DspSecretDto;
import top.openadexchange.dto.DspSettingDto;
import top.openadexchange.dto.DspSettingDto.DspTargetingDto;
import top.openadexchange.dto.query.DspQueryDto;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.mos.application.converter.DspConverter;
import top.openadexchange.mos.application.factory.UserFactory;

@Service
@Slf4j
public class DspService {

    @Resource
    private DspDao dspDao;
    @Resource
    private DspConverter dspConverter;
    @Resource
    private DspSiteAdPlacementDao dspSiteAdPlacementDao;
    @Resource
    private DspTargetingDao dspTargetingDao;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private UserFactory userFactory;

    public Integer addDsp(DspDto dspDto) {
        //创建dsp成功自动创建用户，然后绑定dsp角色
        SysUser sysUser = userFactory.forDsp(dspDto);
        sysUserService.insertUser(sysUser);
        Dsp dsp = dspConverter.from(dspDto);
        dsp.setUserId(sysUser.getUserId());
        dspDao.save(dsp);
        return dsp.getId();
    }

    public Boolean updateDsp(DspDto dspDto) {
        Dsp dsp = dspConverter.from(dspDto);
        return dspDao.updateById(dsp);
    }

    public Boolean deleteDsp(Integer id) {
        log.info("deleteDsp: {}", id);
        Dsp dsp = dspDao.getById(id);
        sysUserService.deleteUserById(dsp.getUserId());
        dspDao.removeById(id);
        return true;
    }

    public DspDto getDsp(Integer id) {
        return dspConverter.toDspDto(dspDao.getById(id));
    }

    public Boolean enableDsp(Integer id) {
        return dspDao.enableDsp(id);
    }

    public Boolean disableDsp(Integer id) {
        return dspDao.disableDsp(id);
    }

    public Page<Dsp> pageListDsp(DspQueryDto queryDto) {
        return dspDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create().eq(Dsp::getName, queryDto.getName()).eq(Dsp::getStatus, queryDto.getStatus()));
    }

    @Transactional
    public Boolean dspSetting(Integer id, DspSettingDto dspSettingDto) {
        List<Integer> siteAdPlacementIds = dspSettingDto.getSiteAdPlacementIds();
        settingDspSiteAdPlacements(id, siteAdPlacementIds);
        dspDao.settingDspQpsLimit(id, dspSettingDto.getQpsLimit());

        DspTargetingDto dspTargetingDto = dspSettingDto.getTargeting();
        DspTargeting dspTargeting = new DspTargeting();
        dspTargeting.setDspId(id);
        if (dspTargetingDto.getOsList() != null) {
            dspTargeting.setOs(JSON.toJSONString(dspTargetingDto.getOsList()));
        }
        if (dspTargetingDto.getDeviceTypes() != null) {
            dspTargeting.setDeviceType(JSON.toJSONString(dspTargetingDto.getDeviceTypes()));
        }
        if (dspTargetingDto.getRegions() != null) {
            dspTargeting.setRegion(JSON.toJSONString(dspTargetingDto.getRegions()));
        }
        dspTargetingDao.save(dspTargeting);
        return true;
    }

    public void settingDspSiteAdPlacements(Integer id, List<Integer> siteAdPlacementIds) {
        dspSiteAdPlacementDao.addDspSiteAdPlacements(id, siteAdPlacementIds);
    }

    public DspSettingDto getDspSetting(Integer dspId) {
        DspSettingDto dspSettingDto = new DspSettingDto();
        dspSettingDto.setSiteAdPlacementIds(dspSiteAdPlacementDao.getDspSiteAdPlacementIds(dspId));
        dspSettingDto.setQpsLimit(dspDao.getDspQpsLimit(dspId));
        DspTargeting dspTargeting = dspTargetingDao.getDspTargeting(dspId);
        if (dspTargeting != null) {
            DspSettingDto.DspTargetingDto targetingDto = new DspSettingDto.DspTargetingDto();
            if (dspTargeting.getOs() != null) {
                targetingDto.setOsList(JSON.parseArray(dspTargeting.getOs(), String.class));
            }
            //            targetingDto.setCountry(dspTargeting.getCountry());
            if (dspTargeting.getDeviceType() != null) {
                targetingDto.setDeviceTypes(JSON.parseArray(dspTargeting.getDeviceType(), String.class));
            }
            if (dspTargeting.getRegion() != null) {
                targetingDto.setRegions(JSON.parseArray(dspTargeting.getRegion(), String.class));
            }
            dspSettingDto.setTargeting(targetingDto);
        }
        return dspSettingDto;
    }

    public DspSecretDto getDspSecret(Integer dspId) {
        Assert.notNull(dspId, "dspId cannot be null");

        Dsp dsp = dspDao.getById(dspId);
        DspSecretDto dspSecretDto = new DspSecretDto();
        dspSecretDto.setApiToken(dsp.getToken());
        dspSecretDto.setEncryptionKey(dsp.getEncryptionKey());
        dspSecretDto.setIntegrityKey(dsp.getIntegrityKey());
        return dspSecretDto;
    }
}