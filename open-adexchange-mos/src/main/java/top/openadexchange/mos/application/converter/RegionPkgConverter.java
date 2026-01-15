package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;
import top.openadexchange.dto.RegionPkgDto;
import top.openadexchange.model.RegionPkg;

@Component
public class RegionPkgConverter {

    public RegionPkg from(RegionPkgDto regionPkgDto) {
        if (regionPkgDto == null) {
            return null;
        }
        RegionPkg regionPkg = new RegionPkg();
        regionPkg.setId(regionPkgDto.getId());
        regionPkg.setName(regionPkgDto.getName());
        regionPkg.setType(regionPkgDto.getType());
        return regionPkg;
    }

    public RegionPkgDto toRegionPkgDto(RegionPkg regionPkg) {
        if (regionPkg == null) {
            return null;
        }
        RegionPkgDto regionPkgDto = new RegionPkgDto();
        regionPkgDto.setId(regionPkg.getId());
        regionPkgDto.setName(regionPkg.getName());
        regionPkgDto.setType(regionPkg.getType());
        return regionPkgDto;
    }
}
