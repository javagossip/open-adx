package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.AdPlacementDto;
import top.openadexchange.model.AdPlacement;

@Mapper(componentModel = "spring")
public interface AdPlacementConverter {

    AdPlacement from(AdPlacementDto adPlacementDto);

    AdPlacementDto toAdPlacementDto(AdPlacement adPlacement);
}