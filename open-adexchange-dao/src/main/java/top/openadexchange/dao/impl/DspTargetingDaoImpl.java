package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.DspTargetingDao;
import top.openadexchange.mapper.DspTargetingMapper;
import top.openadexchange.model.DspTargeting;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Service
public class DspTargetingDaoImpl extends ServiceImpl<DspTargetingMapper, DspTargeting> implements DspTargetingDao {

    @Override
    public DspTargeting getDspTargeting(Integer dspId) {
        return getOne(QueryWrapper.create().eq(DspTargeting::getDspId, dspId));
    }
}
