package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.SecurityUtils;

import jakarta.annotation.Resource;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.dto.AdvertiserDto;
import top.openadexchange.model.Advertiser;

@Component
public class AdvertiserConverter {

    @Resource
    protected EntityCodeService entityCodeService;

    public Advertiser from(AdvertiserDto advertiserDto) {
        if (advertiserDto == null) {
            return null;
        }

        Advertiser advertiser = new Advertiser();
        advertiser.setId(advertiserDto.getId());
        advertiser.setAgencyId(advertiserDto.getAgencyId());
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
        advertiser.setStatus(advertiserDto.getStatus());

        // 设置code和userId
        if (entityCodeService != null) {
            advertiser.setCode(entityCodeService.generateAdvertiserCode());
        }
        advertiser.setUserId(SecurityUtils.getUserId());

        return advertiser;
    }

    public AdvertiserDto toAdvertiserDto(Advertiser advertiser) {
        if (advertiser == null) {
            return null;
        }

        AdvertiserDto advertiserDto = new AdvertiserDto();
        advertiserDto.setId(advertiser.getId());
        advertiserDto.setAgencyId(advertiser.getAgencyId());
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
        advertiserDto.setStatus(advertiser.getStatus());

        return advertiserDto;
    }
}