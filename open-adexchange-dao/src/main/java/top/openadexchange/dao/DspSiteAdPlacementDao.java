package top.openadexchange.dao;

import java.util.List;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.DspSiteAdPlacement;

/**
 * dsp和媒体广告位的绑定关系(dsp需要哪些广告位流量) 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
public interface DspSiteAdPlacementDao extends IService<DspSiteAdPlacement> {

    void addDspSiteAdPlacements(Long id, List<Long> siteAdPlacementIds);

    List<DspSiteAdPlacement> getDspSiteAdPlacements(Long dspId);

    List<Long> getDspSiteAdPlacementIds(Long dspId);
}
