package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.NativeAsset;
import top.openadexchange.mapper.NativeAssetMapper;
import top.openadexchange.dao.NativeAssetDao;
import org.springframework.stereotype.Service;

/**
 * Native模板字段定义 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Service
public class NativeAssetDaoImpl extends ServiceImpl<NativeAssetMapper, NativeAsset>  implements NativeAssetDao{

}
