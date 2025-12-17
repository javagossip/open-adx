package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.NativeAssetDto;
import top.openadexchange.model.NativeAsset;

@Mapper(componentModel = "spring")
public interface NativeAssetConverter {

    NativeAsset from(NativeAssetDto nativeAssetDto);

    NativeAssetDto toNativeAssetDto(NativeAsset nativeAsset);
}