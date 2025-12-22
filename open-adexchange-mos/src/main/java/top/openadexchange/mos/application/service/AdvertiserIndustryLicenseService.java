package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.dao.AdvertiserIndustryLicenseDao;
import top.openadexchange.dto.AdvertiserIndustryLicenseDto;
import top.openadexchange.model.AdvertiserIndustryLicense;
import top.openadexchange.mos.application.converter.AdvertiserIndustryLicenseConverter;

@Service
@Slf4j
public class AdvertiserIndustryLicenseService {

    @Resource
    private AdvertiserIndustryLicenseDao advertiserIndustryLicenseDao;
    @Resource
    private AdvertiserIndustryLicenseConverter advertiserIndustryLicenseConverter;

    public Long addLicense(AdvertiserIndustryLicenseDto licenseDto) {
        log.info("addLicense: {}", licenseDto);
        AdvertiserIndustryLicense license = advertiserIndustryLicenseConverter.from(licenseDto);
        advertiserIndustryLicenseDao.save(license);
        return license.getId();
    }

    public Boolean updateLicense(AdvertiserIndustryLicenseDto licenseDto) {
        log.info("updateLicense: {}", licenseDto);
        AdvertiserIndustryLicense license = advertiserIndustryLicenseConverter.from(licenseDto);
        return advertiserIndustryLicenseDao.updateById(license);
    }

    public Boolean deleteLicense(Long id) {
        log.info("deleteLicense: {}", id);
        return advertiserIndustryLicenseDao.removeById(id);
    }

    public AdvertiserIndustryLicenseDto getLicense(Long id) {
        return advertiserIndustryLicenseConverter.toDto(advertiserIndustryLicenseDao.getById(id));
    }

    public List<AdvertiserIndustryLicenseDto> getLicensesByAdvertiserId(Long advertiserId) {
        List<AdvertiserIndustryLicense> licenses = advertiserIndustryLicenseDao.list(QueryWrapper.create()
                .eq(AdvertiserIndustryLicense::getAdvertiserId, advertiserId));
        return licenses.stream().map(advertiserIndustryLicenseConverter::toDto).toList();
    }
}