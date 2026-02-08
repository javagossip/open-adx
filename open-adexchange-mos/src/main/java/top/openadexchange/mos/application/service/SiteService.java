package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.constants.enums.DomainEventType;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.dto.SiteDto;
import top.openadexchange.dto.query.SiteQueryDto;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.mos.application.converter.SiteConverter;
import top.openadexchange.mos.application.factory.DomainEventFactory;

@Service
public class SiteService {

    @Resource
    private SiteDao siteDao;
    @Resource
    private SiteConverter siteConverter;
    @Resource
    private DomainEventDao domainEventDao;
    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;

    @Transactional
    public Long addSite(SiteDto siteDto) {
        Site site = siteConverter.from(siteDto);
        siteDao.save(site);
        domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_CREATED.name(), site.getId()));
        return site.getId();
    }

    @Transactional
    public Boolean updateSite(SiteDto siteDto) {
        Site site = siteConverter.from(siteDto);
        boolean updated = siteDao.updateById(site);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_UPDATED.name(), site.getId()));
        }
        return updated;
    }

    @Transactional
    public Boolean deleteSite(Long id) {
        boolean siteAdPlacementsExists =
                siteAdPlacementDao.exists(QueryWrapper.create().eq(SiteAdPlacement::getSiteId, id));
        Assert.isTrue(!siteAdPlacementsExists, "Site ad placements exists");

        boolean deleted = siteDao.removeById(id);
        if (deleted) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_DELETED.name(), id));
        }
        return deleted;
    }

    public SiteDto getSite(Long id) {
        return siteConverter.toSiteDto(siteDao.getById(id));
    }

    @Transactional
    public Boolean enableSite(Long id) {
        boolean enabled = siteDao.enableSite(id);
        if (enabled) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_UPDATED.name(), id));
        }
        return enabled;
    }

    public Page<Site> pageListSites(SiteQueryDto siteQueryDto) {
        return siteDao.page(Page.of(siteQueryDto.getPageNo(), siteQueryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(Site::getName, siteQueryDto.getName())
                        .eq(Site::getSiteType, siteQueryDto.getType())
                        .eq(Site::getPlatform,
                                siteQueryDto.getPlatform() == null ? null : siteQueryDto.getPlatform().name())
                        .eq(Site::getStatus, siteQueryDto.getStatus()));
    }

    @Transactional
    public Boolean disableSite(Long id) {
        boolean disabled = siteDao.disableSite(id);
        if (disabled) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.SITE_UPDATED.name(), id));
        }
        return disabled;
    }

    public List<Site> searchSites(String searchKey, Integer size) {
        return siteDao.list(QueryWrapper.create().like(Site::getName, searchKey).limit(size == null ? 20 : size));
    }
}
