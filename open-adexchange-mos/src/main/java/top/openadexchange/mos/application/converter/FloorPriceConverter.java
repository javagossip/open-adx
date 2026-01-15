package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;

import top.openadexchange.commons.FloorPriceUtils;
import top.openadexchange.dto.FloorPriceDto;
import top.openadexchange.model.FloorPrice;

@Component
public class FloorPriceConverter {

    public FloorPrice from(FloorPriceDto floorPriceDto) {
        if (floorPriceDto == null) {
            return null;
        }

        FloorPrice floorPrice = new FloorPrice();
        floorPrice.setSiteAdPlacementId(floorPriceDto.getSiteAdPlacementId());
        floorPrice.setFloorPrice(FloorPriceUtils.yuanToCent(floorPriceDto.getFloorPrice()));
        floorPrice.setIndustryId(floorPriceDto.getIndustryId());
        floorPrice.setRegionLevelId(floorPriceDto.getRegionLevelId());
        floorPrice.setStatus(1); // 默认启用

        return floorPrice;
    }

    public FloorPriceDto toDto(FloorPrice floorPrice) {
        return null;
    }
}