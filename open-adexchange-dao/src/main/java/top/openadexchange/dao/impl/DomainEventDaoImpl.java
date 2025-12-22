package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

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

}
