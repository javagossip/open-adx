package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.model.DspStat;
import top.openadexchange.mapper.DspStatMapper;
import top.openadexchange.dao.DspStatDao;

import org.springframework.stereotype.Service;

import java.util.List;

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
