package top.openadexchange.dao;

import java.util.List;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.AdvertiserIndustryLicense;

/**
 * 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
public interface AdvertiserIndustryLicenseDao extends IService<AdvertiserIndustryLicense> {

    List<AdvertiserIndustryLicense> getByAdvertiserId(Long id);
}
