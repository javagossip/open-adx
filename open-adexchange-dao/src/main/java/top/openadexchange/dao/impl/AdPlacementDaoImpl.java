package top.openadexchange.dao.impl;

import com.mybatisflex.core.query.QueryWrapper;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import org.springframework.util.Assert;

import top.openadexchange.dao.AdPlacementDao;
import top.openadexchange.mapper.AdPlacementMapper;
import top.openadexchange.model.AdPlacement;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
@Service
public class AdPlacementDaoImpl extends ServiceImpl<AdPlacementMapper, AdPlacement> implements AdPlacementDao {

    public Boolean enableAdPlacement(Integer id) {
        return updateChain().set(AdPlacement::getStatus, 1).eq(AdPlacement::getId, id).update();
    }

    public Boolean disableAdPlacement(Integer id) {
        return updateChain().set(AdPlacement::getStatus, 0).eq(AdPlacement::getId, id).update();
    }

    @Override
    public AdPlacement getByTemplateCode(String nativeTemplateCode) {
        Assert.notNull(nativeTemplateCode, "nativeTemplateCode cannot be null");
        return getOne(QueryWrapper.create().eq(AdPlacement::getCode, nativeTemplateCode));
    }
}