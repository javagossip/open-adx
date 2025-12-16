package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.mapper.SiteAdPlacementMapper;
import top.openadexchange.dao.SiteAdPlacementDao;
import org.springframework.stereotype.Service;

/**
 * 媒体广告位管理 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Service
public class SiteAdPlacementDaoImpl extends ServiceImpl<SiteAdPlacementMapper, SiteAdPlacement>  implements SiteAdPlacementDao{

    public Boolean enableSiteAdPlacement(Long id) {
        return updateChain().set(SiteAdPlacement::getStatus, 1).eq(SiteAdPlacement::getId, id).update();
    }

    public Boolean disableSiteAdPlacement(Long id) {
        return updateChain().set(SiteAdPlacement::getStatus, 0).eq(SiteAdPlacement::getId, id).update();
    }
}