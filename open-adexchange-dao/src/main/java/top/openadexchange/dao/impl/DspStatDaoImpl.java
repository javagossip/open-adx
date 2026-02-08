package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.DspStatDao;
import top.openadexchange.mapper.DspStatMapper;
import top.openadexchange.model.DspStat;

/**
 * Dsp统计表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-29
 */
@Service
public class DspStatDaoImpl extends ServiceImpl<DspStatMapper, DspStat> implements DspStatDao {

    @Override
    public void saveBatchOnDuplicateKeyUpdate(List<DspStat> dspStats) {
        getMapper().saveBatchOnDuplicateKeyUpdate(dspStats);
    }
}
