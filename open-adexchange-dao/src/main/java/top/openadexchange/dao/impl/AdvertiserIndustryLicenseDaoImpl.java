package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.AdvertiserIndustryLicenseDao;
import top.openadexchange.mapper.AdvertiserIndustryLicenseMapper;
import top.openadexchange.model.AdvertiserIndustryLicense;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
@Service
public class AdvertiserIndustryLicenseDaoImpl
        extends ServiceImpl<AdvertiserIndustryLicenseMapper, AdvertiserIndustryLicense>
        implements AdvertiserIndustryLicenseDao {

    @Override
    public List<AdvertiserIndustryLicense> getByAdvertiserId(Long advertiserId) {
        return list(QueryWrapper.create().eq(AdvertiserIndustryLicense::getAdvertiserId, advertiserId));
    }

    @Override
    public void saveAdvertiserIndustryLicenses(Long advertiserId,
            List<AdvertiserIndustryLicense> advertiserIndustryLicenses) {
        if (advertiserIndustryLicenses == null) {
            return;
        }
        advertiserIndustryLicenses.forEach(advertiserIndustryLicense -> advertiserIndustryLicense.setAdvertiserId(
                advertiserId));
        saveBatch(advertiserIndustryLicenses);
    }
}
