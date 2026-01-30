package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.DspStat;

import java.util.List;

/**
 * Dsp统计表 服务层。
 *
 * @author top.openadexchange
 * @since 2026-01-29
 */
public interface DspStatDao extends IService<DspStat> {

    void saveBatchOnDuplicateKeyUpdate(List<DspStat> dspStats);
}
