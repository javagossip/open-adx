package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.SecurityUtils;

import jakarta.annotation.Resource;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.dto.SiteAdPlacementDto;
import top.openadexchange.model.SiteAdPlacement;

@Component
public class SiteAdPlacementConverter {

    @Resource
    protected EntityCodeService entityCodeService;

    public SiteAdPlacement from(SiteAdPlacementDto siteAdPlacementDto) {
        if (siteAdPlacementDto == null) {
            return null;
        }

        SiteAdPlacement siteAdPlacement = new SiteAdPlacement();
        siteAdPlacement.setId(siteAdPlacementDto.getId());
        siteAdPlacement.setSiteId(siteAdPlacementDto.getSiteId());
        siteAdPlacement.setAdPlacementId(siteAdPlacementDto.getAdPlacementId());
        siteAdPlacement.setPlatform(siteAdPlacementDto.getPlatform());
        siteAdPlacement.setName(siteAdPlacementDto.getName());
        siteAdPlacement.setDemoUrl(siteAdPlacementDto.getDemoUrl());
        siteAdPlacement.setStatus(siteAdPlacementDto.getStatus());

        // 设置code和userId
        if (entityCodeService != null) {
            siteAdPlacement.setCode(entityCodeService.generateSiteAdPlacementCode());
        }
        siteAdPlacement.setUserId(SecurityUtils.getUserId());

        return siteAdPlacement;
    }

    public SiteAdPlacementDto toSiteAdPlacementDto(SiteAdPlacement siteAdPlacement) {
        if (siteAdPlacement == null) {
            return null;
        }

        SiteAdPlacementDto siteAdPlacementDto = new SiteAdPlacementDto();
        siteAdPlacementDto.setId(siteAdPlacement.getId());
        siteAdPlacementDto.setSiteId(siteAdPlacement.getSiteId());
        siteAdPlacementDto.setAdPlacementId(siteAdPlacement.getAdPlacementId());
        siteAdPlacementDto.setPlatform(siteAdPlacement.getPlatform());
        siteAdPlacementDto.setName(siteAdPlacement.getName());
        siteAdPlacementDto.setDemoUrl(siteAdPlacement.getDemoUrl());
        siteAdPlacementDto.setStatus(siteAdPlacement.getStatus());

        return siteAdPlacementDto;
    }
}