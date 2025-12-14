package top.openadexchange.mos.application.service;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.dto.SiteDto;
import top.openadexchange.dto.query.SiteQueryDto;
import top.openadexchange.model.Site;
import top.openadexchange.mos.application.converter.SiteConverter;

@Service
public class SiteService {

    @Resource
    private SiteDao siteDao;
    @Resource
    private SiteConverter siteConverter;

    public Long addSite(SiteDto siteDto) {
        Site site = siteConverter.from(siteDto);
        siteDao.save(site);
        return site.getId();
    }

    public Boolean updateSite(SiteDto siteDto) {
        Site site = siteConverter.from(siteDto);
        return siteDao.updateById(site);
    }

    public Boolean deleteSite(Long id) {
        return siteDao.removeById(id);
    }

    public SiteDto getSite(Long id) {
        return siteConverter.toSiteDto(siteDao.getById(id));
    }

    public Boolean enableSite(Long id) {
        return siteDao.enableSite(id);
    }

    public Page<Site> pageListSites(SiteQueryDto siteQueryDto) {
        return siteDao.page(Page.of(siteQueryDto.getPageNo(), siteQueryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(Site::getName, siteQueryDto.getName())
                        .eq(Site::getSiteType, siteQueryDto.getType())
                        .eq(Site::getStatus, siteQueryDto.getStatus()));
    }

    public Boolean disableSite(Long id) {
        return siteDao.disableSite(id);
    }
}
