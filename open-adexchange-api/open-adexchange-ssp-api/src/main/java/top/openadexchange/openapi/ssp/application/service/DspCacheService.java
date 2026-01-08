package top.openadexchange.openapi.ssp.application.service;

import top.openadexchange.model.Dsp;

/**
 * DSP缓存服务接口
 * 提供对DSP信息的缓存操作
 */
public interface DspCacheService {

    /**
     * 根据DSP ID获取DSP信息
     * @param dspId DSP ID
     * @return DSP信息
     */
    Dsp getDspById(Integer dspId);

    /**
     * 更新缓存中的DSP信息
     * @param dsp DSP信息
     */
    void updateDspCache(Dsp dsp);

    /**
     * 清空全部DSP缓存
     */
    void clearAllDspCache();

    /**
     * 删除指定DSP的缓存
     * @param dspId DSP ID
     */
    void deleteDspCache(Long dspId);
}