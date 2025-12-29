package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.Industry;

/**
 * 广告行业字典表 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-29
 */
@Mapper
public interface IndustryMapper extends BaseMapper<Industry> {

}
