package top.openadexchange.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.DspSiteAdPlacementDao;
import top.openadexchange.mapper.DspSiteAdPlacementMapper;
import top.openadexchange.model.DspSiteAdPlacement;

/**
 * dsp和媒体广告位的绑定关系(dsp需要哪些广告位流量) 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Service
public class DspSiteAdPlacementDaoImpl extends ServiceImpl<DspSiteAdPlacementMapper, DspSiteAdPlacement>
        implements DspSiteAdPlacementDao {

    @Override
    public void addDspSiteAdPlacements(Integer id, List<Integer> siteAdPlacementIds) {
        remove(QueryWrapper.create().eq(DspSiteAdPlacement::getDspId, id));
        saveBatch(siteAdPlacementIds.stream().map(siteAdPlacementId -> {
            DspSiteAdPlacement dspSiteAdPlacement = new DspSiteAdPlacement();
            dspSiteAdPlacement.setDspId(id);
            dspSiteAdPlacement.setSiteAdPlacementId(siteAdPlacementId);
            return dspSiteAdPlacement;
        }).collect(Collectors.toList()));
    }

    @Override
    public List<DspSiteAdPlacement> getDspSiteAdPlacements(Integer dspId) {
        return list(QueryWrapper.create().eq(DspSiteAdPlacement::getDspId, dspId));
    }

    @Override
    public List<Integer> getDspSiteAdPlacementIds(Integer dspId) {
        return getDspSiteAdPlacements(dspId).stream()
                .map(DspSiteAdPlacement::getSiteAdPlacementId)
                .collect(Collectors.toList());
    }
}
