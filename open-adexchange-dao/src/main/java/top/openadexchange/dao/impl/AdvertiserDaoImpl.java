package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.AdvertiserDao;
import top.openadexchange.mapper.AdvertiserMapper;
import top.openadexchange.model.Advertiser;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
@Service
public class AdvertiserDaoImpl extends ServiceImpl<AdvertiserMapper, Advertiser> implements AdvertiserDao {

    @Override
    public Advertiser getByDspAdvertiserId(String advertiserId) {
        return getOne(QueryWrapper.create().eq(Advertiser::getDspAdvertiserId, advertiserId));
    }

    @Override
    public List<Advertiser> getAdvertisersByDspAdvertiserIds(List<String> advertiserIds) {
        return list(QueryWrapper.create().in(Advertiser::getDspAdvertiserId, advertiserIds));
    }
}
