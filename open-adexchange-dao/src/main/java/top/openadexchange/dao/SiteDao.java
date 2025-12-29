package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;
import top.openadexchange.model.Site;

/**
 *  服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-29
 */
public interface SiteDao extends IService<Site> {

    Boolean enableSite(Long id);

    Boolean disableSite(Long id);
}
