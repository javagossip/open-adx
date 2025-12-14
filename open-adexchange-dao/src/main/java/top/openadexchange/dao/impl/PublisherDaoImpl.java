package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
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

}
