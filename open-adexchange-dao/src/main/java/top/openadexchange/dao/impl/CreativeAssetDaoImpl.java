package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.CreativeAssetDao;
import top.openadexchange.mapper.CreativeAssetMapper;
import top.openadexchange.model.CreativeAsset;

/**
 * 创意素材资产表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-03
 */
@Service
public class CreativeAssetDaoImpl extends ServiceImpl<CreativeAssetMapper, CreativeAsset>  implements CreativeAssetDao{

}
