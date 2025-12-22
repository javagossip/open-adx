package top.openadexchange.openapi.dsp.application.converter;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.model.Advertiser;
import top.openadexchange.model.AdvertiserIndustryLicense;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserIndustryLicenseDto;
import top.openadexchange.openapi.dsp.application.dto.AuditResult;

@Component
public class AdvertiserConverter {

    @Resource
    private EntityCodeService entityCodeService;

    public Advertiser from(AdvertiserDto advertiserDto) {
        if (advertiserDto == null) {
            return null;
        }

        Advertiser advertiser = new Advertiser();
        advertiser.setDspAdvertiserId(advertiserDto.getAdvertiserId());
        advertiser.setAdvertiserName(advertiserDto.getAdvertiserName());
        advertiser.setCompanyName(advertiserDto.getCompanyName());
        advertiser.setBusinessLicenseNo(advertiserDto.getBusinessLicenseNo());
        advertiser.setIndustryCode(advertiserDto.getIndustryCode());
        advertiser.setRegisteredAddress(advertiserDto.getRegisteredAddress());
        advertiser.setContactName(advertiserDto.getContactName());
        advertiser.setContactPhone(advertiserDto.getContactPhone());
        advertiser.setContactEmail(advertiserDto.getContactEmail());
        advertiser.setLegalPersonName(advertiserDto.getLegalPersonName());
        advertiser.setBusinessLicenseUrl(advertiserDto.getBusinessLicenseUrl());
        advertiser.setLegalPersonIdUrl(advertiserDto.getLegalPersonIdUrl());
        advertiser.setCode(entityCodeService.generateAdvertiserCode());
        return advertiser;
    }

    public AdvertiserDto toAdvertiserDto(Advertiser advertiser) {
        if (advertiser == null) {
            return null;
        }

        AdvertiserDto advertiserDto = new AdvertiserDto();
        advertiserDto.setAdvertiserName(advertiser.getAdvertiserName());
        advertiserDto.setCompanyName(advertiser.getCompanyName());
        advertiserDto.setBusinessLicenseNo(advertiser.getBusinessLicenseNo());
        advertiserDto.setIndustryCode(advertiser.getIndustryCode());
        advertiserDto.setRegisteredAddress(advertiser.getRegisteredAddress());
        advertiserDto.setContactName(advertiser.getContactName());
        advertiserDto.setContactPhone(advertiser.getContactPhone());
        advertiserDto.setContactEmail(advertiser.getContactEmail());
        advertiserDto.setLegalPersonName(advertiser.getLegalPersonName());
        advertiserDto.setBusinessLicenseUrl(advertiser.getBusinessLicenseUrl());
        advertiserDto.setLegalPersonIdUrl(advertiser.getLegalPersonIdUrl());

        return advertiserDto;
    }

    public List<AdvertiserIndustryLicense> fromAdvertiserLicenses(List<AdvertiserIndustryLicenseDto> advertiserIndustryLicenses) {
        return advertiserIndustryLicenses.stream().map(this::fromAdvertiserLicense).filter(Objects::nonNull).toList();
    }

    public AdvertiserIndustryLicense fromAdvertiserLicense(AdvertiserIndustryLicenseDto advertiserIndustryLicenseDto) {
        if (advertiserIndustryLicenseDto == null) {
            return null;
        }
        AdvertiserIndustryLicense advertiserIndustryLicense = new AdvertiserIndustryLicense();
        //        advertiserIndustryLicense.setAdvertiserId();
        advertiserIndustryLicense.setIndustryCode(advertiserIndustryLicenseDto.getIndustryCode());
        advertiserIndustryLicense.setLicenseName(advertiserIndustryLicenseDto.getLicenseName());
        advertiserIndustryLicense.setLicenseUrl(advertiserIndustryLicenseDto.getLicenseUrl());
        return advertiserIndustryLicense;
    }

    public AdvertiserAuditResultDto toAdvertiserAuditResultDto(Advertiser advertiser) {
        if (advertiser == null) {
            return null;
        }

        AdvertiserAuditResultDto advertiserAuditDto = new AdvertiserAuditResultDto();
        advertiserAuditDto.setAdvertiserId(advertiser.getDspAdvertiserId());

        AuditResult auditResult = new AuditResult();
        auditResult.setAuditStatus(advertiser.getAuditStatus());
        auditResult.setAuditReason(advertiser.getAuditReason());
        if (advertiser.getAuditTime() != null) {
            auditResult.setAuditTime(advertiser.getAuditTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli());
        }
        return advertiserAuditDto;
    }
}