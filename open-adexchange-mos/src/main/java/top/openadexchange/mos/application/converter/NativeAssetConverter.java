package top.openadexchange.mos.application.converter;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import top.openadexchange.dto.adspecification.NativeAdSpecificationDto;
import top.openadexchange.dto.adspecification.NativeAssetDto;
import top.openadexchange.model.NativeAsset;

@Component
public class NativeAssetConverter {

    public NativeAsset from(NativeAssetDto nativeAssetDto) {
        if (nativeAssetDto == null) {
            return null;
        }

        NativeAsset nativeAsset = new NativeAsset();
        nativeAsset.setId(nativeAssetDto.getId());
        nativeAsset.setAdPlacementId(nativeAssetDto.getAdPlacementId());
        nativeAsset.setAssetName(nativeAssetDto.getAssetName());
        nativeAsset.setAssetKey(nativeAssetDto.getAssetKey());
        nativeAsset.setAssetType(nativeAssetDto.getAssetType());
        nativeAsset.setRequired(nativeAssetDto.getRequired());
        nativeAsset.setRepeatable(nativeAssetDto.getRepeatable());
        nativeAsset.setMaxLength(nativeAssetDto.getMaxLength());
        nativeAsset.setMinLength(nativeAssetDto.getMinLength());
        nativeAsset.setWidth(nativeAssetDto.getWidth());
        nativeAsset.setHeight(nativeAssetDto.getHeight());
        nativeAsset.setRatio(nativeAssetDto.getRatio());
        nativeAsset.setMimeTypes(nativeAssetDto.getMimeTypes());
        nativeAsset.setMaxSizeKb(nativeAssetDto.getMaxSizeKb());

        return nativeAsset;
    }

    public NativeAssetDto toNativeAssetDto(NativeAsset nativeAsset) {
        if (nativeAsset == null) {
            return null;
        }

        NativeAssetDto nativeAssetDto = new NativeAssetDto();
        nativeAssetDto.setId(nativeAsset.getId());
        nativeAssetDto.setAdPlacementId(nativeAsset.getAdPlacementId());
        nativeAssetDto.setAssetName(nativeAsset.getAssetName());
        nativeAssetDto.setAssetKey(nativeAsset.getAssetKey());
        nativeAssetDto.setAssetType(nativeAsset.getAssetType());
        nativeAssetDto.setRequired(nativeAsset.getRequired());
        nativeAssetDto.setRepeatable(nativeAsset.getRepeatable());
        nativeAssetDto.setMaxLength(nativeAsset.getMaxLength());
        nativeAssetDto.setMinLength(nativeAsset.getMinLength());
        nativeAssetDto.setWidth(nativeAsset.getWidth());
        nativeAssetDto.setHeight(nativeAsset.getHeight());
        nativeAssetDto.setRatio(nativeAsset.getRatio());
        nativeAssetDto.setMimeTypes(nativeAsset.getMimeTypes());
        nativeAssetDto.setMaxSizeKb(nativeAsset.getMaxSizeKb());

        return nativeAssetDto;
    }

    public List<NativeAsset> from(Long adPlacementId, NativeAdSpecificationDto nativeAdSpecDto) {
        if (nativeAdSpecDto == null) {
            return null;
        }
        List<NativeAssetDto> nativeAssetDtoList = nativeAdSpecDto.getNativeAssets();
        nativeAssetDtoList.forEach(nativeAssetDto -> nativeAssetDto.setAdPlacementId(adPlacementId));
        return nativeAssetDtoList.stream().map(this::from).filter(Objects::nonNull).toList();
    }

    public NativeAdSpecificationDto toNativeAssetDtoList(List<NativeAsset> nativeAssets) {
        NativeAdSpecificationDto nativeAdSpecDto = new NativeAdSpecificationDto();
        nativeAdSpecDto.setNativeAssets(nativeAssets.stream()
                .map(this::toNativeAssetDto)
                .filter(Objects::nonNull)
                .toList());
        return nativeAdSpecDto;
    }
}