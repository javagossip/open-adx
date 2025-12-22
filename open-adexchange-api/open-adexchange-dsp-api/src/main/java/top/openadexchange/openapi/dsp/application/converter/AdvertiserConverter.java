package top.openadexchange.openapi.dsp.application.converter;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.model.Advertiser;
import top.openadexchange.openapi.dsp.application.dto.AdvertiserDto;

@Component
public class AdvertiserConverter {

    @Resource
    private EntityCodeService entityCodeService;

    public Advertiser from(AdvertiserDto advertiserDto) {
        if (advertiserDto == null) {
            return null;
        }

        Advertiser advertiser = new Advertiser();
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
        advertiser.setAuditStatus(advertiserDto.getAuditStatus());
        advertiser.setAuditReason(advertiserDto.getAuditReason());
        advertiser.setAuditTime(advertiserDto.getAuditTime());

        // 设置code
        if (entityCodeService != null) {
            advertiser.setCode(entityCodeService.generateAdvertiserCode());
        }
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
        advertiserDto.setAuditStatus(advertiser.getAuditStatus());
        advertiserDto.setAuditReason(advertiser.getAuditReason());
        advertiserDto.setAuditTime(advertiser.getAuditTime());

        return advertiserDto;
    }
}