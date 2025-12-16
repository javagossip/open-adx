package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.DspTargeting;

/**
 * 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
public interface DspTargetingDao extends IService<DspTargeting> {

    //根据dspId获取dsp定向信息
    DspTargeting getDspTargeting(Long dspId);
}
