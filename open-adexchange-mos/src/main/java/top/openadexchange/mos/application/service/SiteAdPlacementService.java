package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.constants.enums.DomainEventType;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.dao.DspPlacementMappingDao;
import top.openadexchange.dao.DspSiteAdPlacementDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteAdpAdtMappingDao;
import top.openadexchange.dto.SiteAdPlacementDto;
import top.openadexchange.dto.query.SiteAdPlacementQueryDto;
import top.openadexchange.model.DspPlacementMapping;
import top.openadexchange.model.DspSiteAdPlacement;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.mos.application.converter.SiteAdPlacementConverter;
import top.openadexchange.mos.application.factory.DomainEventFactory;

@Service
public class SiteAdPlacementService {

    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;
    @Resource
    private SiteAdpAdtMappingDao siteAdpAdtMappingDao;
    @Resource
    private SiteAdPlacementConverter siteAdPlacementConverter;
    @Resource
    private DomainEventDao domainEventDao;
    @Resource
    private DspPlacementMappingDao dspPlacementMappingDao;
    @Resource
    private DspSiteAdPlacementDao dspSiteAdPlacementDao;

    @Transactional
    public Integer addSiteAdPlacement(SiteAdPlacementDto siteAdPlacementDto) {
        SiteAdPlacement siteAdPlacement = siteAdPlacementConverter.from(siteAdPlacementDto);
        siteAdPlacementDao.save(siteAdPlacement);
        siteAdpAdtMappingDao.saveAdpAdtMappings(siteAdPlacement.getId(), siteAdPlacementDto.getAdPlacementIds());
        domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_AD_PLACEMENT_CREATED.name(),
                siteAdPlacement.getId()));
        return siteAdPlacement.getId();
    }

    @Transactional
    public Boolean updateSiteAdPlacement(SiteAdPlacementDto siteAdPlacementDto) {
        SiteAdPlacement siteAdPlacement = siteAdPlacementConverter.from(siteAdPlacementDto);
        boolean updated = siteAdPlacementDao.updateById(siteAdPlacement);
        if (updated) {
            siteAdpAdtMappingDao.saveAdpAdtMappings(siteAdPlacement.getId(), siteAdPlacementDto.getAdPlacementIds());
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_AD_PLACEMENT_UPDATED.name(),
                    siteAdPlacement.getId()));
        }
        return updated;
    }

    @Transactional
    public Boolean deleteSiteAdPlacement(Long id) {
        boolean dspRelatedSiteAdp =
                dspSiteAdPlacementDao.exists(QueryWrapper.create().eq(DspSiteAdPlacement::getSiteAdPlacementId, id));
        Assert.isTrue(!dspRelatedSiteAdp, "媒体广告位被DSP使用，不能删除");
        boolean dspPlacementMappingExists =
                dspPlacementMappingDao.exists(QueryWrapper.create().eq(DspPlacementMapping::getSiteAdPlacementId, id));
        Assert.isTrue(!dspPlacementMappingExists, "媒体广告位和dsp广告位之间存在映射关系，不能删除");

        boolean deleted = siteAdPlacementDao.removeById(id);
        if (deleted) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_AD_PLACEMENT_DELETED.name(), id));
        }
        return deleted;
    }

    public SiteAdPlacementDto getSiteAdPlacement(Integer id) {
        SiteAdPlacement siteAdPlacement = siteAdPlacementDao.getById(id);
        SiteAdPlacementDto siteAdPlacementDto = siteAdPlacementConverter.toSiteAdPlacementDto(siteAdPlacement);
        siteAdPlacementDto.setAdPlacementIds(siteAdpAdtMappingDao.getAdpAdtMappingIds(id));

        return siteAdPlacementDto;
    }

    @Transactional
    public Boolean enableSiteAdPlacement(Long id) {
        boolean updated = siteAdPlacementDao.enableSiteAdPlacement(id);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_AD_PLACEMENT_UPDATED.name(), id));
        }
        return updated;
    }

    @Transactional
    public Boolean disableSiteAdPlacement(Long id) {
        boolean updated = siteAdPlacementDao.disableSiteAdPlacement(id);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_AD_PLACEMENT_UPDATED.name(), id));
        }
        return updated;
    }

    public Page<SiteAdPlacement> pageListSiteAdPlacements(SiteAdPlacementQueryDto queryDto) {
        return siteAdPlacementDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(SiteAdPlacement::getSiteId, queryDto.getSiteId())
                        .eq(SiteAdPlacement::getAdPlacementId, queryDto.getAdPlacementId())
                        .eq(SiteAdPlacement::getStatus, queryDto.getStatus()));
    }

    public List<SiteAdPlacement> getSiteAdPlacements(List<Long> siteAdPlacementIds) {
        return siteAdPlacementDao.list(QueryWrapper.create().in(SiteAdPlacement::getId, siteAdPlacementIds));
    }

    public List<SiteAdPlacement> searchSiteAdPlacements(String searchKey, Integer size) {
        if (!StringUtils.hasText(searchKey)) {
            return siteAdPlacementDao.list(QueryWrapper.create().limit(size == null ? 100 : size));
        }
        return siteAdPlacementDao.list(QueryWrapper.create()
                .like(SiteAdPlacement::getName, searchKey)
                .limit(size == null ? 100 : size));
    }
}