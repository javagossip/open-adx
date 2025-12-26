package top.openadexchange.openapi.dsp.application.validator;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.commons.AssertUtils;
import top.openadexchange.dao.AdvertiserDao;
import top.openadexchange.model.Advertiser;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;

@Component
@Slf4j
public class AdvertiserValidator {

    @Resource
    private AdvertiserDao advertiserDao;

    public void validateForAddAdvertiser(AdvertiserDto advertiserDto) {
        String dspAdvertiserId = advertiserDto.getAdvertiserId();
        AssertUtils.notBlank(dspAdvertiserId, ApiErrorCode.ADVERTISER_ID_IS_REQUIRED);
        AssertUtils.notBlank(advertiserDto.getAdvertiserName(), ApiErrorCode.ADVERTISER_NAME_IS_REQUIRED);
        AssertUtils.notBlank(advertiserDto.getCompanyName(), ApiErrorCode.COMPANY_NAME_IS_REQUIRED);
        AssertUtils.notBlank(advertiserDto.getIndustryCode(), ApiErrorCode.INDUSTRY_CODE_IS_REQUIRED);
        AssertUtils.notBlank(advertiserDto.getBusinessLicenseNo(), ApiErrorCode.BUSINESS_LICENSE_NO_IS_REQUIRED);
        AssertUtils.notBlank(advertiserDto.getBusinessLicenseUrl(), ApiErrorCode.BUSINESS_LICENSE_URL_IS_REQUIRED);

        Advertiser advertiser = advertiserDao.getByDspAdvertiserId(dspAdvertiserId);
        if (advertiser != null) {
            log.error("广告主已存在, dspAdvertiserId: {}", dspAdvertiserId);
            throw ApiErrorCode.ADVERTISER_EXISTS.toException();
        }
    }

    public void validateForUpdateAdvertiser(AdvertiserDto advertiserDto) {
        String dspAdvertiserId = advertiserDto.getAdvertiserId();
        AssertUtils.notBlank(dspAdvertiserId, ApiErrorCode.ADVERTISER_ID_IS_REQUIRED);

        Advertiser advertiser = advertiserDao.getByDspAdvertiserId(dspAdvertiserId);
        if (advertiser == null) {
            log.error("广告主不存在, dspAdvertiserId: {}", dspAdvertiserId);
            throw ApiErrorCode.ADVERTISER_NOT_EXIST.toException();
        }
    }
}
