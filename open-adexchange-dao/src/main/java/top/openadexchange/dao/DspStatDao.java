package top.openadexchange.dao;

import java.util.List;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.DspStat;

/**
 * Dsp统计表 服务层。
 *
 * @author top.openadexchange
 * @since 2026-01-29
 */
public interface DspStatDao extends IService<DspStat> {

    void saveBatchOnDuplicateKeyUpdate(List<DspStat> dspStats);
}
