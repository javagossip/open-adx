package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.Advertiser;

import java.util.List;

/**
 * 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
public interface AdvertiserDao extends IService<Advertiser> {

    Advertiser getByDspAdvertiserId(String advertiserId);

    List<Advertiser> getAdvertisersByDspAdvertiserIds(List<String> advertiserIds);
}
