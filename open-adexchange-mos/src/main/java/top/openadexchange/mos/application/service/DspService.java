package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.enums.DomainEventType;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.DspPlacementMappingDao;
import top.openadexchange.dao.DspSiteAdPlacementDao;
import top.openadexchange.dao.DspTargetingDao;
import top.openadexchange.dto.DspDto;
import top.openadexchange.dto.DspPlacementMappingDto;
import top.openadexchange.dto.DspPlacementMappingQuery;
import top.openadexchange.dto.DspSecretDto;
import top.openadexchange.dto.DspSettingDto;
import top.openadexchange.dto.DspSettingDto.DspTargetingDto;
import top.openadexchange.dto.query.DspQueryDto;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspPlacementMapping;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.model.enums.RtbProtocolType;
import top.openadexchange.mos.application.converter.DspConverter;
import top.openadexchange.mos.application.factory.DomainEventFactory;
import top.openadexchange.mos.application.factory.UserFactory;

import static top.openadexchange.model.table.DspPlacementMappingTableDef.*;
import static top.openadexchange.model.table.DspTableDef.*;
import static top.openadexchange.model.table.SiteAdPlacementTableDef.*;

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
    private DspPlacementMappingDao dspPlacementMappingDao;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private UserFactory userFactory;
    @Resource
    private DomainEventDao domainEventDao;

    @Transactional
    public Integer addDsp(DspDto dspDto) {
        //创建dsp成功自动创建用户，然后绑定dsp角色
        SysUser sysUser = userFactory.forDsp(dspDto);
        sysUserService.insertUser(sysUser);
        Dsp dsp = dspConverter.from(dspDto);
        dsp.setUserId(sysUser.getUserId());
        dspDao.save(dsp);

        domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_CREATED.name(), dsp.getId()));
        return dsp.getId();
    }

    public Boolean updateDsp(DspDto dspDto) {
        Dsp dsp = dspConverter.from(dspDto);
        boolean updated = dspDao.updateById(dsp);

        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_UPDATED.name(), dsp.getId()));
        }
        return updated;
    }

    @Transactional
    public Boolean deleteDsp(Integer id) {
        log.info("deleteDsp: {}", id);
        Dsp dsp = dspDao.getById(id);
        Assert.notNull(dsp, "dsp not exists");
        if (dsp.getUserId() != null) {
            log.info("Delete dsp user: {}", dsp.getUserId());
            sysUserService.deleteUserById(dsp.getUserId());
        }
        log.info("Delete dsp placement mappings: {}", id);
        dspPlacementMappingDao.remove(QueryWrapper.create().eq(DspPlacementMapping::getDspId, id));
        boolean deleted = dspDao.removeById(id);
        if (deleted) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_DELETED.name(), dsp.getId()));
        }
        return true;
    }

    @Transactional
    public Boolean batchDeleteDsp(List<Integer> ids) {
        log.info("batchDeleteDsp: {}", ids);
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        for (Integer id : ids) {
            deleteDsp(id);
        }
        return true;
    }

    public DspDto getDsp(Integer id) {
        return dspConverter.toDspDto(dspDao.getById(id));
    }

    @Transactional
    public Boolean enableDsp(Integer id) {
        boolean enabled = dspDao.enableDsp(id);
        if (enabled) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_UPDATED.name(), id));
        }
        return enabled;
    }

    @Transactional
    public Boolean disableDsp(Integer id) {
        boolean disabled = dspDao.disableDsp(id);
        if (disabled) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_UPDATED.name(), id));
        }
        return disabled;
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
        dspTargetingDao.updateDspTargeting(dspTargeting);
        domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_UPDATED.name(), id));
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

    public List<Dsp> search(String searchKey) {
        if (!StringUtils.hasText(searchKey)) {
            return dspDao.list(QueryWrapper.create()
                    .eq(Dsp::getStatus, 1)
                    .eq(Dsp::getRtbProtocolType, RtbProtocolType.CUSTOM.getValue())
                    .limit(20));
        }
        //这里限制最多100条，防止搜索结果过多
        return dspDao.list(QueryWrapper.create().like(Dsp::getName, searchKey).limit(100));
    }

    public Page<DspPlacementMappingDto> pageDspPlacementMappings(DspPlacementMappingQuery query) {
        return dspPlacementMappingDao.pageAs(Page.of(query.getPageNo(), query.getPageSize()),
                QueryWrapper.create()
                        .select("t1.id",
                                "t1.dsp_id as dspId",
                                "t3.dsp_id as dspCode",
                                "t3.name as dspName",
                                "t1.site_ad_placement_id",
                                "t2.name as siteAdPlacementName",
                                "t2.code as siteAdPlacementCode",
                                "t1.dsp_slot_id")
                        .from(DSP_PLACEMENT_MAPPING)
                        .as("t1")
                        .leftJoin(SITE_AD_PLACEMENT)
                        .as("t2")
                        .on("t1.site_ad_placement_id = t2.id")
                        .leftJoin(DSP)
                        .as("t3")
                        .on("t1.dsp_id = t3.id")
                        .eq(DspPlacementMapping::getDspSlotId, query.getDspSlotId())
                        .eq(Dsp::getDspId, query.getDspCode())
                        .eq(SiteAdPlacement::getCode, query.getSiteAdPlacementCode()),
                DspPlacementMappingDto.class);
    }

    @Transactional
    public Integer addDspPlacementMapping(DspPlacementMapping dspPlacementMapping) {
        Assert.notNull(dspPlacementMapping.getDspId(), "dspId cannot be null");
        Assert.notNull(dspPlacementMapping.getSiteAdPlacementId(), "Please select a site ad placement");
        boolean mappingExists = dspPlacementMappingDao.exists(QueryWrapper.create()
                .eq(DspPlacementMapping::getDspId, dspPlacementMapping.getDspId())
                .eq(DspPlacementMapping::getSiteAdPlacementId, dspPlacementMapping.getSiteAdPlacementId())
                .eq(DspPlacementMapping::getDspSlotId, dspPlacementMapping.getDspSlotId()));
        Assert.isTrue(!mappingExists, "The mapping already exists");

        dspPlacementMappingDao.save(dspPlacementMapping);
        domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_UPDATED.name(),
                dspPlacementMapping.getDspId()));
        return dspPlacementMapping.getId();
    }

    @Transactional
    public Boolean deleteDspPlacementMapping(Integer id) {
        Assert.notNull(id, "id cannot be null");
        DspPlacementMapping origDspPlacementMapping = dspPlacementMappingDao.getById(id);
        Assert.notNull(origDspPlacementMapping, "dspPlacementMapping is null");
        log.info("Delete dspPlacementMapping: {}", id);
        boolean updated = dspPlacementMappingDao.removeById(id);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_UPDATED.name(),
                    origDspPlacementMapping.getDspId()));
        }
        return updated;
    }

    @Transactional
    public Boolean updateDspPlacementMapping(DspPlacementMapping dspPlacementMapping) {
        Assert.notNull(dspPlacementMapping.getId(), "id cannot be null");
        log.info("updateDspPlacementMapping: {}", dspPlacementMapping);

        boolean updated = dspPlacementMappingDao.updateById(dspPlacementMapping);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.DSP_UPDATED.name(),
                    dspPlacementMapping.getDspId()));
        }
        return updated;
    }
}