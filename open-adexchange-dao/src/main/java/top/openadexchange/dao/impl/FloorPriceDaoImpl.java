package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.FloorPrice;
import top.openadexchange.mapper.FloorPriceMapper;
import top.openadexchange.dao.FloorPriceDao;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-14
 */
@Service
public class FloorPriceDaoImpl extends ServiceImpl<FloorPriceMapper, FloorPrice>  implements FloorPriceDao{

}
