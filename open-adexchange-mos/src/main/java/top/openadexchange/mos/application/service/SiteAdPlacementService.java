package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dto.SiteAdPlacementDto;
import top.openadexchange.dto.query.SiteAdPlacementQueryDto;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.mos.application.converter.SiteAdPlacementConverter;

@Service
public class SiteAdPlacementService {

    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;
    @Resource
    private SiteAdPlacementConverter siteAdPlacementConverter;

    public Long addSiteAdPlacement(SiteAdPlacementDto siteAdPlacementDto) {
        SiteAdPlacement siteAdPlacement = siteAdPlacementConverter.from(siteAdPlacementDto);
        siteAdPlacementDao.save(siteAdPlacement);
        return siteAdPlacement.getId();
    }

    public Boolean updateSiteAdPlacement(SiteAdPlacementDto siteAdPlacementDto) {
        SiteAdPlacement siteAdPlacement = siteAdPlacementConverter.from(siteAdPlacementDto);
        return siteAdPlacementDao.updateById(siteAdPlacement);
    }

    public Boolean deleteSiteAdPlacement(Long id) {
        return siteAdPlacementDao.removeById(id);
    }

    public SiteAdPlacementDto getSiteAdPlacement(Long id) {
        return siteAdPlacementConverter.toSiteAdPlacementDto(siteAdPlacementDao.getById(id));
    }

    public Boolean enableSiteAdPlacement(Long id) {
        return siteAdPlacementDao.enableSiteAdPlacement(id);
    }

    public Boolean disableSiteAdPlacement(Long id) {
        return siteAdPlacementDao.disableSiteAdPlacement(id);
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
        return siteAdPlacementDao.list(QueryWrapper.create()
                .like(SiteAdPlacement::getName, searchKey)
                .limit(size == null ? 20 : size));
    }
}