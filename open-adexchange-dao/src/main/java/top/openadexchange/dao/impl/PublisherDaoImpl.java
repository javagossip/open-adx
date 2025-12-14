package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.PublisherDao;
import top.openadexchange.mapper.PublisherMapper;
import top.openadexchange.model.Publisher;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-13
 */
@Service
public class PublisherDaoImpl extends ServiceImpl<PublisherMapper, Publisher> implements PublisherDao {

    @Override
    public Boolean enablePublisher(Long id) {
        return updateChain().set(Publisher::getStatus, 1).eq(Publisher::getId, id).update();
    }

    @Override
    public Boolean disablePublisher(Long id) {
        return updateChain().set(Publisher::getStatus, 0).eq(Publisher::getId, id).update();
    }
}
