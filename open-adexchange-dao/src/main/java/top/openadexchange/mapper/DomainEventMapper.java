package top.openadexchange.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.mybatisflex.core.BaseMapper;

import top.openadexchange.model.DomainEvent;

/**
 * 映射层。
 *
 * @author top.openadexchange
 * @since 2025-12-16
 */
@Mapper
public interface DomainEventMapper extends BaseMapper<DomainEvent> {

}
