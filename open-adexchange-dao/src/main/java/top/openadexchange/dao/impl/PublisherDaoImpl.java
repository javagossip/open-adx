package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;
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
    public Boolean enablePublisher(Integer id) {
        return updateChain().set(Publisher::getStatus, 1).eq(Publisher::getId, id).update();
    }

    @Override
    public Boolean disablePublisher(Integer id) {
        return updateChain().set(Publisher::getStatus, 0).eq(Publisher::getId, id).update();
    }

    @Override
    public List<Publisher> pageList(int pageNo, int pageSize) {
        List<Publisher> publisherList =
                list(QueryWrapper.create().eq(Publisher::getStatus, 1).limit((pageNo - 1) * pageSize, pageSize));
        return publisherList;
    }
}
