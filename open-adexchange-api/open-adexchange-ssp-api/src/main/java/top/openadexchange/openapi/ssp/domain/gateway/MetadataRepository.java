package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Site;
import top.openadexchange.model.SiteAdPlacement;

import java.util.List;

/**
 * 广告元数据仓储接口，用来获取广告元数据，包括：dsp、广告位、创意、站点/app以及广告模版等信息
 */
@ExtensionPoint
public interface MetadataRepository {
    /**
     * 根据tagId获取广告位信息
     *
     * @param tagId 广告位编码
     * @return 广告位信息
     */
    SiteAdPlacement getSiteAdPlacementByTagId(String tagId);

    /**
     * 获取站点/app信息
     *
     * @param siteId 站点ID
     * @return 站点信息
     */
    Site getSite(Long siteId);

    List<DspAggregate> getDspByIds(List<Integer> dspIds);

    AdPlacement getAdPlacement(Integer id);

    Dsp getDspByDspId(String dspId);
}
