package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.Advertiser;
import top.openadexchange.mapper.AdvertiserMapper;
import top.openadexchange.dao.AdvertiserDao;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
@Service
public class AdvertiserDaoImpl extends ServiceImpl<AdvertiserMapper, Advertiser>  implements AdvertiserDao{

}
