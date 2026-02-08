package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.SiteAdpAdtMappingDao;
import top.openadexchange.mapper.SiteAdpAdtMappingMapper;
import top.openadexchange.model.SiteAdpAdtMapping;

/**
 * 媒体广告位-广告模板关联表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-19
 */
@Service
public class SiteAdpAdtMappingDaoImpl extends ServiceImpl<SiteAdpAdtMappingMapper, SiteAdpAdtMapping>
        implements SiteAdpAdtMappingDao {

    @Override
    public void saveAdpAdtMappings(Integer siteAdPlacementId, List<Integer> adPlacementIds) {
        //先删除关联，再重新添加关联
        remove(QueryWrapper.create().eq(SiteAdpAdtMapping::getSiteAdPlacementId, siteAdPlacementId));
        if (adPlacementIds == null || adPlacementIds.isEmpty()) {
            return;
        }
        saveBatch(adPlacementIds.stream().map(adPlacementId -> {
            SiteAdpAdtMapping mapping = new SiteAdpAdtMapping();
            mapping.setSiteAdPlacementId(siteAdPlacementId);
            mapping.setAdPlacementId(adPlacementId);
            return mapping;
        }).toList());
    }

    @Override
    public List<Integer> getAdpAdtMappingIds(Integer siteAdPlacementId) {
        return list(QueryWrapper.create().eq(SiteAdpAdtMapping::getSiteAdPlacementId, siteAdPlacementId)).stream()
                .map(SiteAdpAdtMapping::getAdPlacementId)
                .toList();
    }

    @Override
    public List<SiteAdpAdtMapping> listBySiteAdPlacementIds(List<Integer> siteAdPlacementIds) {
        Assert.notEmpty(siteAdPlacementIds, "siteAdPlacementIds cannot be empty");
        return list(QueryWrapper.create().in(SiteAdpAdtMapping::getSiteAdPlacementId, siteAdPlacementIds));
    }
}
