package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.SiteAdPlacementDto;
import top.openadexchange.model.SiteAdPlacement;

@Mapper(componentModel = "spring")
public interface SiteAdPlacementConverter {

    SiteAdPlacement from(SiteAdPlacementDto siteAdPlacementDto);

    SiteAdPlacementDto toSiteAdPlacementDto(SiteAdPlacement siteAdPlacement);
}