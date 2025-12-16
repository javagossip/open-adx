package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.District;

/**
 * 数据来源：https://github.com/eduosi/district.git 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Mapper
public interface DistrictMapper extends BaseMapper<District> {

}
