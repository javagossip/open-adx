package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.DspDao;
import top.openadexchange.mapper.DspMapper;
import top.openadexchange.model.Dsp;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-13
 */
@Service
public class DspDaoImpl extends ServiceImpl<DspMapper, Dsp> implements DspDao {

    public Boolean enableDsp(Long id) {
        return updateChain().set(Dsp::getStatus, 1).eq(Dsp::getId, id).update();
    }

    public Boolean disableDsp(Long id) {
        return updateChain().set(Dsp::getStatus, 0).eq(Dsp::getId, id).update();
    }

    @Override
    public void settingDspQpsLimit(Long id, Integer qpsLimit) {
        updateChain().set(Dsp::getQpsLimit, qpsLimit).eq(Dsp::getId, id).update();
    }

    @Override
    public Integer getDspQpsLimit(Long dspId) {
        return getByIdOpt(dspId).map(Dsp::getQpsLimit).orElse(null);
    }
}