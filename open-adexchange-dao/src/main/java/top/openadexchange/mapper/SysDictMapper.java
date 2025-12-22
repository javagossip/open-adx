package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.mybatisflex.core.BaseMapper;

import top.openadexchange.model.SysDict;

/**
 * 字典数据表 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {

}
