package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;
import top.openadexchange.model.Creative;

import java.util.List;

/**
 * 广告创意表，包括adx 自有创意以及 dsp 平台创意 服务层。
 *
 * @author top.openadexchange
 * @since 2026-01-03
 */
public interface CreativeDao extends IService<Creative> {

    List<Creative> getCreativesByDspCreativeIds(List<String> creativeIds);
}
