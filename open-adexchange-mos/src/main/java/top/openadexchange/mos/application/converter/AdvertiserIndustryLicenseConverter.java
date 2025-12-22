package top.openadexchange.mos.application.converter;


import org.springframework.stereotype.Component;

import top.openadexchange.dto.AdvertiserIndustryLicenseDto;
import top.openadexchange.model.AdvertiserIndustryLicense;

@Component
public class AdvertiserIndustryLicenseConverter {

    public AdvertiserIndustryLicense from(AdvertiserIndustryLicenseDto advertiserIndustryLicenseDto) {
        AdvertiserIndustryLicense advertiserIndustryLicense = new AdvertiserIndustryLicense();
        advertiserIndustryLicense.setId(advertiserIndustryLicenseDto.getId());
        advertiserIndustryLicense.setAdvertiserId(advertiserIndustryLicenseDto.getAdvertiserId());
        advertiserIndustryLicense.setIndustryCode(advertiserIndustryLicenseDto.getIndustryCode());
        advertiserIndustryLicense.setLicenseName(advertiserIndustryLicenseDto.getLicenseName());
        advertiserIndustryLicense.setLicenseUrl(advertiserIndustryLicenseDto.getLicenseUrl());

        return advertiserIndustryLicense;
    }

    public AdvertiserIndustryLicenseDto toDto(AdvertiserIndustryLicense license) {
        AdvertiserIndustryLicenseDto licenseDto = new AdvertiserIndustryLicenseDto();
        licenseDto.setId(license.getId());
        licenseDto.setAdvertiserId(license.getAdvertiserId());
        licenseDto.setIndustryCode(license.getIndustryCode());
        licenseDto.setLicenseName(license.getLicenseName());
        licenseDto.setLicenseUrl(license.getLicenseUrl());

        return licenseDto;
    }
}