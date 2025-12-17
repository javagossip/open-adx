package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.AdvertiserDto;
import top.openadexchange.model.Advertiser;

@Mapper(componentModel = "spring")
public interface AdvertiserConverter {

    Advertiser from(AdvertiserDto advertiserDto);

    AdvertiserDto toAdvertiserDto(Advertiser advertiser);
}