package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.SiteDto;
import top.openadexchange.model.Site;

@Mapper(componentModel = "spring")
public interface SiteConverter {

    Site from(SiteDto siteDto);

    SiteDto toSiteDto(Site site);
}
