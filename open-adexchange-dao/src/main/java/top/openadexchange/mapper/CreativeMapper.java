package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.Creative;

/**
 * 广告创意表，包括adx 自有创意以及 dsp 平台创意 映射层。
 *
 * @author top.openadexchange
 * @since 2026-01-03
 */
@Mapper
public interface CreativeMapper extends BaseMapper<Creative> {

}
