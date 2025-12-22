package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;
import top.openadexchange.model.Dsp;

/**
 *  服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-13
 */
public interface DspDao extends IService<Dsp> {

    Boolean enableDsp(Long id);

    Boolean disableDsp(Long id);

    void settingDspQpsLimit(Long id, Integer qpsLimit);

    Integer getDspQpsLimit(Long dspId);

    Dsp getDspByToken(String token);
}