package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.NativeAssetDao;
import top.openadexchange.mapper.NativeAssetMapper;
import top.openadexchange.model.NativeAsset;

/**
 * Native模板字段定义 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Service
public class NativeAssetDaoImpl extends ServiceImpl<NativeAssetMapper, NativeAsset> implements NativeAssetDao {

    @Override
    public void updateNativeAssetsByAdPlacementId(Long adPlacementId, List<NativeAsset> nativeAssets) {
        remove(QueryWrapper.create().eq(NativeAsset::getAdPlacementId, adPlacementId));
        nativeAssets.forEach(nativeAsset -> nativeAsset.setAdPlacementId(adPlacementId));
        saveBatch(nativeAssets);
    }

    @Override
    public List<NativeAsset> listByAdPlacementId(Long adPlacementId) {
        return list(QueryWrapper.create().eq(NativeAsset::getAdPlacementId, adPlacementId));
    }
}
