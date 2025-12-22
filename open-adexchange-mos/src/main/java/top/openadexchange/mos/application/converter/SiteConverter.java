package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ruoyi.common.utils.SecurityUtils;

import top.openadexchange.dto.SiteDto;
import top.openadexchange.model.Site;

@Component
public class SiteConverter {

    public Site from(SiteDto siteDto) {
        if (siteDto == null) {
            return null;
        }

        Site site = new Site();
        site.setId(siteDto.getId());
        site.setPublisherId(siteDto.getPublisherId());
        site.setName(siteDto.getName());
        site.setDomain(siteDto.getDomain());
        site.setAppId(siteDto.getAppId());
        site.setAppBundle(siteDto.getAppBundle());
        site.setPlatform(siteDto.getPlatform());
        site.setSiteType(siteDto.getSiteType());
        site.setStatus(siteDto.getStatus());
        
        // 设置userId
        site.setUserId(SecurityUtils.getUserId());
        
        return site;
    }

    public SiteDto toSiteDto(Site site) {
        if (site == null) {
            return null;
        }

        SiteDto siteDto = new SiteDto();
        siteDto.setId(site.getId());
        siteDto.setPublisherId(site.getPublisherId());
        siteDto.setName(site.getName());
        siteDto.setDomain(site.getDomain());
        siteDto.setAppId(site.getAppId());
        siteDto.setAppBundle(site.getAppBundle());
        siteDto.setPlatform(site.getPlatform());
        siteDto.setSiteType(site.getSiteType());
        siteDto.setStatus(site.getStatus());
        
        return siteDto;
    }
}