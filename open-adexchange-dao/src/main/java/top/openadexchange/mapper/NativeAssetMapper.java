package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.mybatisflex.core.BaseMapper;
import top.openadexchange.model.NativeAsset;

/**
 * Native模板字段定义 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-17
 */
@Mapper
public interface NativeAssetMapper extends BaseMapper<NativeAsset> {

}
