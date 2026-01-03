package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.CreativeAsset;
import top.openadexchange.mapper.CreativeAssetMapper;
import top.openadexchange.dao.CreativeAssetDao;
import org.springframework.stereotype.Service;

/**
 * 创意素材资产表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-03
 */
@Service
public class CreativeAssetDaoImpl extends ServiceImpl<CreativeAssetMapper, CreativeAsset>  implements CreativeAssetDao{

}
