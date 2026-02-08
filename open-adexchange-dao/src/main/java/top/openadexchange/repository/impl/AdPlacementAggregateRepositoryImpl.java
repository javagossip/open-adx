package top.openadexchange.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import top.openadexchange.dao.AdPlacementDao;
import top.openadexchange.dao.NativeAssetDao;
import top.openadexchange.domain.entity.AdPlacementAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.NativeAsset;
import top.openadexchange.repository.AdPlacementAggregateRepository;

@Repository
public class AdPlacementAggregateRepositoryImpl implements AdPlacementAggregateRepository {

    @Autowired
    private NativeAssetDao nativeAssetDao;
    @Autowired
    private AdPlacementDao adPlacementDao;

    @Override
    public AdPlacementAggregate getAdPlacementAggregate(Integer adPlacementId) {
        Assert.notNull(adPlacementId, "adPlacementId cannot be null");
        AdPlacement adPlacement = adPlacementDao.getById(adPlacementId);
        List<NativeAsset> nativeAssets = nativeAssetDao.listByAdPlacementId(adPlacementId);

        AdPlacementAggregate adPlacementAggregate = new AdPlacementAggregate();
        adPlacementAggregate.setAdPlacement(adPlacement);
        adPlacementAggregate.setNativeAssets(nativeAssets);

        return adPlacementAggregate;
    }
}
