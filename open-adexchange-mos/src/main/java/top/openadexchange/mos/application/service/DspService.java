package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspSiteAdPlacementDao;
import top.openadexchange.dao.DspTargetingDao;
import top.openadexchange.dto.DspDto;
import top.openadexchange.dto.DspSettingDto;
import top.openadexchange.dto.DspSettingDto.DspTargetingDto;
import top.openadexchange.dto.query.DspQueryDto;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.mos.application.converter.DspConverter;

@Service
public class DspService {

    @Resource
    private DspDao dspDao;
    @Resource
    private DspConverter dspConverter;
    @Resource
    private DspSiteAdPlacementDao dspSiteAdPlacementDao;
    @Resource
    private DspTargetingDao dspTargetingDao;

    public Long addDsp(DspDto dspDto) {
        Dsp dsp = dspConverter.from(dspDto);
        dspDao.save(dsp);
        return dsp.getId();
    }

    public Boolean updateDsp(DspDto dspDto) {
        Dsp dsp = dspConverter.from(dspDto);
        return dspDao.updateById(dsp);
    }

    public Boolean deleteDsp(Long id) {
        return dspDao.removeById(id);
    }

    public DspDto getDsp(Long id) {
        return dspConverter.toDspDto(dspDao.getById(id));
    }

    public Boolean enableDsp(Long id) {
        return dspDao.enableDsp(id);
    }

    public Boolean disableDsp(Long id) {
        return dspDao.disableDsp(id);
    }

    public Page<Dsp> pageListDsp(DspQueryDto queryDto) {
        return dspDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create().eq(Dsp::getName, queryDto.getName()).eq(Dsp::getStatus, queryDto.getStatus()));
    }

    @Transactional
    public Boolean dspSetting(Long id, DspSettingDto dspSettingDto) {
        List<Long> siteAdPlacementIds = dspSettingDto.getSiteAdPlacementIds();
        settingDspSiteAdPlacements(id, siteAdPlacementIds);
        dspDao.settingDspQpsLimit(id, dspSettingDto.getQpsLimit());

        DspTargetingDto dspTargetingDto = dspSettingDto.getTargeting();
        DspTargeting dspTargeting = new DspTargeting();
        dspTargeting.setDspId(id);
        if (dspTargetingDto.getOsList() != null) {
            dspTargeting.setOs(JSON.toJSONString(dspTargetingDto.getOsList()));
        }
        dspTargeting.setCountry(dspTargetingDto.getCountry());
        if (dspTargetingDto.getDeviceTypes() != null) {
            dspTargeting.setDeviceType(JSON.toJSONString(dspTargetingDto.getDeviceTypes()));
        }
        if (dspTargetingDto.getRegions() != null) {
            dspTargeting.setRegion(JSON.toJSONString(dspTargetingDto.getRegions()));
        }
        dspTargetingDao.save(dspTargeting);
        return true;
    }

    public void settingDspSiteAdPlacements(Long id, List<Long> siteAdPlacementIds) {
        dspSiteAdPlacementDao.addDspSiteAdPlacements(id, siteAdPlacementIds);
    }

    public DspSettingDto getDspSetting(Long dspId) {
        DspSettingDto dspSettingDto = new DspSettingDto();
        dspSettingDto.setSiteAdPlacementIds(dspSiteAdPlacementDao.getDspSiteAdPlacementIds(dspId));
        dspSettingDto.setQpsLimit(dspDao.getDspQpsLimit(dspId));
        DspTargeting dspTargeting = dspTargetingDao.getDspTargeting(dspId);
        if (dspTargeting != null) {
            DspSettingDto.DspTargetingDto targetingDto = new DspSettingDto.DspTargetingDto();
            if (dspTargeting.getOs() != null) {
                targetingDto.setOsList(JSON.parseArray(dspTargeting.getOs(), String.class));
            }
            targetingDto.setCountry(dspTargeting.getCountry());
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
}