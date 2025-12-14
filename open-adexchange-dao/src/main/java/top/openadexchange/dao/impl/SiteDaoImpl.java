package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.Site;
import top.openadexchange.mapper.SiteMapper;
import top.openadexchange.dao.SiteDao;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
@Service
public class SiteDaoImpl extends ServiceImpl<SiteMapper, Site>  implements SiteDao{

}
