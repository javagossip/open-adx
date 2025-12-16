package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;
import top.openadexchange.model.SiteAdPlacement;

/**
 * 媒体广告位管理 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
public interface SiteAdPlacementDao extends IService<SiteAdPlacement> {

    Boolean enableSiteAdPlacement(Long id);

    Boolean disableSiteAdPlacement(Long id);
}