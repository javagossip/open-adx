package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.Publisher;

/**
 * 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-13
 */
public interface PublisherDao extends IService<Publisher> {

    Boolean enablePublisher(Long id);

    Boolean disablePublisher(Long id);
}
