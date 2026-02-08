package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.FloorPriceDao;
import top.openadexchange.mapper.FloorPriceMapper;
import top.openadexchange.model.FloorPrice;

/**
 *  服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-14
 */
@Service
public class FloorPriceDaoImpl extends ServiceImpl<FloorPriceMapper, FloorPrice>  implements FloorPriceDao{

}
