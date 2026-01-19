package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;
import top.openadexchange.model.SiteAdpAdtMapping;

import java.util.List;

/**
 * 媒体广告位-广告模板关联表 服务层。
 *
 * @author top.openadexchange
 * @since 2026-01-19
 */
public interface SiteAdpAdtMappingDao extends IService<SiteAdpAdtMapping> {

    void saveAdpAdtMappings(Integer id, List<Integer> adPlacementIds);

    List<Integer> getAdpAdtMappingIds(Integer siteAdPlacementId);

    List<SiteAdpAdtMapping> listBySiteAdPlacementIds(List<Integer> siteAdPlacementIds);
}
