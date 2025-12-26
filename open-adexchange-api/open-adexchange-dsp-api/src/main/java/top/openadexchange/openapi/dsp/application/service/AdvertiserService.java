package top.openadexchange.openapi.dsp.application.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.commons.AssertUtils;
import top.openadexchange.dao.AdvertiserDao;
import top.openadexchange.dao.AdvertiserIndustryLicenseDao;
import top.openadexchange.model.Advertiser;
import top.openadexchange.model.AdvertiserIndustryLicense;
import top.openadexchange.openapi.dsp.application.converter.AdvertiserConverter;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;
import top.openadexchange.openapi.dsp.application.validator.AdvertiserValidator;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;

@Service
public class AdvertiserService {

    @Resource
    private AdvertiserConverter advertiserConverter;
    @Resource
    private AdvertiserValidator advertiserValidator;
    @Resource
    private AdvertiserDao advertiserDao;
    @Resource
    private AdvertiserIndustryLicenseDao advertiserIndustryLicenseDao;

    public String addAdvertiser(AdvertiserDto advertiserDto) {
        advertiserValidator.validateForAddAdvertiser(advertiserDto);

        Advertiser advertiser = advertiserConverter.from(advertiserDto);
        List<AdvertiserIndustryLicense> advertiserIndustryLicenses =
                advertiserConverter.fromAdvertiserLicenses(advertiserDto.getAdvertiserIndustryLicenses());
        advertiserDao.save(advertiser);
        advertiserIndustryLicenseDao.saveAdvertiserIndustryLicenses(advertiser.getId(), advertiserIndustryLicenses);
        return advertiser.getCode();
    }

    public Boolean updateAdvertiser(AdvertiserDto advertiserDto) {
        advertiserValidator.validateForUpdateAdvertiser(advertiserDto);
        return false;
    }

    public AdvertiserAuditResultDto getAuditStatus(String advertiserId) {
        AssertUtils.notBlank(advertiserId, ApiErrorCode.ADVERTISER_ID_IS_REQUIRED);
        Advertiser advertiser = advertiserDao.getByDspAdvertiserId(advertiserId);
        return advertiserConverter.toAdvertiserAuditResultDto(advertiser);
    }

    public List<AdvertiserAuditResultDto> getAuditStatusList(List<String> advertiserIds) {
        List<Advertiser> advertisers = advertiserDao.getAdvertisersByDspAdvertiserIds(advertiserIds);
        return advertisers.stream()
                .map(advertiser -> advertiserConverter.toAdvertiserAuditResultDto(advertiser))
                .filter(Objects::nonNull)
                .toList();
    }
}
