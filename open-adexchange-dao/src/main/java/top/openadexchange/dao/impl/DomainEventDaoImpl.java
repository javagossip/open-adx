package top.openadexchange.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.constants.enums.EventStatus;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.mapper.DomainEventMapper;
import top.openadexchange.model.DomainEvent;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-16
 */
@Service
public class DomainEventDaoImpl extends ServiceImpl<DomainEventMapper, DomainEvent> implements DomainEventDao {

    private static final List<String> EVENT_STATUS_LIST =
            Arrays.asList(EventStatus.PENDING.name(), EventStatus.FAILED.name());

    @Transactional
    @Override
    public List<DomainEvent> listAndUpdateUnHandleEvents(int offset, int limit) {
        List<DomainEvent> domainEvents = list(QueryWrapper.create()
                .in(DomainEvent::getStatus, EVENT_STATUS_LIST)
                .limit(offset, limit));
        domainEvents.forEach(domainEvent -> domainEvent.setStatus(EventStatus.PROCESSING.name()));
        updateBatch(domainEvents);
        return domainEvents;
    }
}
