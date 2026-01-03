package top.openadexchange.openapi.dsp.repository;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

import org.springframework.util.Assert;

import top.openadexchange.dao.CreativeAssetDao;
import top.openadexchange.dao.CreativeDao;
import top.openadexchange.domain.entity.CreativeAggregate;
import top.openadexchange.model.Creative;
import top.openadexchange.model.CreativeAsset;

@Repository
public class CreativeAggregateRepository {

    @Resource
    private CreativeDao creativeDao;
    @Resource
    private CreativeAssetDao creativeAssetDao;

    public void saveCreativeAggregate(CreativeAggregate creativeAggregate) {
        Assert.notNull(creativeAggregate, "creativeAggregate cannot be null");
        Assert.notNull(creativeAggregate.getCreative(), "creativeAggregate.creative cannot be null");
        creativeDao.save(creativeAggregate.getCreative());

        if (creativeAggregate.getCreativeAssets() != null) {
            creativeAssetDao.saveBatch(creativeAggregate.getCreativeAssets());
        }
    }

    public void saveOrUpdateCreativeAggregate(CreativeAggregate creativeAggregate) {
        Assert.notNull(creativeAggregate, "creativeAggregate cannot be null");
        Assert.notNull(creativeAggregate.getCreative(), "creativeAggregate.creative cannot be null");

        Creative creative = creativeAggregate.getCreative();
        String dspCreativeId = creative.getDspCreativeId();

        Creative existingCreative =
                creativeDao.getOne(QueryWrapper.create().eq(Creative::getDspCreativeId, dspCreativeId));
        if (existingCreative != null) {
            creative.setId(existingCreative.getId());
            updateCreativeAggregate(creativeAggregate);
        } else {
            saveCreativeAggregate(creativeAggregate);
        }
    }

    public void updateCreativeAggregate(CreativeAggregate creativeAggregate) {
        Assert.notNull(creativeAggregate.getCreative(), "creativeAggregate.creative cannot be null");
        Assert.notNull(creativeAggregate.getCreative().getId(), "creativeAggregate.creative.id cannot be null");

        creativeDao.updateById(creativeAggregate.getCreative());
        if (creativeAggregate.getCreativeAssets() != null && !creativeAggregate.getCreativeAssets().isEmpty()) {
            creativeAssetDao.remove(QueryWrapper.create()
                    .eq(CreativeAsset::getCreativeId, creativeAggregate.getCreative().getId()));
            creativeAssetDao.saveBatch(creativeAggregate.getCreativeAssets());
        }
    }
}
