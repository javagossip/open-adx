package top.openadexchange.dao;

import java.util.List;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.NativeAsset;

/**
 * Native模板字段定义 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
public interface NativeAssetDao extends IService<NativeAsset> {

    void updateNativeAssetsByAdPlacementId(Long id, List<NativeAsset> nativeAssets);

    List<NativeAsset> listByAdPlacementId(Long adPlacementId);
}
