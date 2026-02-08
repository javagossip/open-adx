package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.DomainEvent;

import java.util.List;

/**
 * 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-16
 */
public interface DomainEventDao extends IService<DomainEvent> {

    List<DomainEvent> listAndUpdateUnHandleEvents(int offset, int limit);
}
