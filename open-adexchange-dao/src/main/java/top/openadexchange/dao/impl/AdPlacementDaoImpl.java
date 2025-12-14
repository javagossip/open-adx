package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.mapper.AdPlacementMapper;
import top.openadexchange.dao.AdPlacementDao;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
@Service
public class AdPlacementDaoImpl extends ServiceImpl<AdPlacementMapper, AdPlacement>  implements AdPlacementDao{

    public Boolean enableAdPlacement(Long id) {
        return updateChain().set(AdPlacement::getStatus, 1).eq(AdPlacement::getId, id).update();
    }

    public Boolean disableAdPlacement(Long id) {
        return updateChain().set(AdPlacement::getStatus, 0).eq(AdPlacement::getId, id).update();
    }
}