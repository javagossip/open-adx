package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.SiteDao;
import top.openadexchange.mapper.SiteMapper;
import top.openadexchange.model.Site;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
@Service
public class SiteDaoImpl extends ServiceImpl<SiteMapper, Site> implements SiteDao {

    @Override
    public Boolean enableSite(Long id) {
        return updateChain().set(Site::getStatus, 1).eq(Site::getId, id).update();
    }

    @Override
    public Boolean disableSite(Long id) {
        return updateChain().set(Site::getStatus, 0).eq(Site::getId, id).update();
    }
}
