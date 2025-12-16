package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.DspSiteAdPlacement;

/**
 * dsp和媒体广告位的绑定关系(dsp需要哪些广告位流量) 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Mapper
public interface DspSiteAdPlacementMapper extends BaseMapper<DspSiteAdPlacement> {

}
