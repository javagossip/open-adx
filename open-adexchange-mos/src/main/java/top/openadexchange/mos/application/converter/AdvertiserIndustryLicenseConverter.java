package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.AdvertiserIndustryLicenseDto;
import top.openadexchange.model.AdvertiserIndustryLicense;

@Mapper(componentModel = "spring")
public interface AdvertiserIndustryLicenseConverter {

    AdvertiserIndustryLicense from(AdvertiserIndustryLicenseDto dto);

    AdvertiserIndustryLicenseDto toDto(AdvertiserIndustryLicense license);
}