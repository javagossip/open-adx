package top.openadexchange.mos.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.openadexchange.dao.RegionPkgDao;
import top.openadexchange.dao.RegionPkgDistrictDao;
import top.openadexchange.dto.RegionPkgDto;
import top.openadexchange.dto.query.RegionPkgQueryDto;
import top.openadexchange.model.RegionPkg;
import top.openadexchange.model.RegionPkgDistrict;
import top.openadexchange.mos.application.converter.RegionPkgConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RegionPkgService {

    @Resource
    private RegionPkgDao regionPkgDao;

    @Resource
    private RegionPkgDistrictDao regionPkgDistrictDao;

    @Resource
    private RegionPkgConverter regionPkgConverter;

    @Transactional(rollbackFor = Exception.class)
    public Integer addRegionPkg(RegionPkgDto regionPkgDto) {
        RegionPkg regionPkg = regionPkgConverter.from(regionPkgDto);
        regionPkgDao.save(regionPkg);
        Integer pkgId = regionPkg.getId();
        addRegionPkgDistricts(pkgId, regionPkgDto.getDistrictCodes());
        return pkgId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRegionPkg(RegionPkgDto regionPkgDto) {
        if (regionPkgDto.getId() == null) {
            throw new IllegalArgumentException("地域包ID不能为空");
        }
        RegionPkg regionPkg = regionPkgConverter.from(regionPkgDto);
        Boolean updated = regionPkgDao.updateById(regionPkg);

        //删除原来的地域关联信息
        regionPkgDistrictDao.remove(QueryWrapper.create().eq(RegionPkgDistrict::getRegionPkgId, regionPkgDto.getId()));
        addRegionPkgDistricts(regionPkgDto.getId(), regionPkgDto.getDistrictCodes());
        return updated;
    }

    private void addRegionPkgDistricts(Integer id, List<String> districtCodes) {
        List<RegionPkgDistrict> districts = null;
        if (districtCodes != null && !districtCodes.isEmpty()) {
            districts = districtCodes.stream().filter(Objects::nonNull).distinct().map(code -> {
                RegionPkgDistrict d = new RegionPkgDistrict();
                d.setRegionPkgId(id);
                d.setDistrictCode(code);
                return d;
            }).collect(Collectors.toList());
        }
        if (districts != null && !districts.isEmpty()) {
            regionPkgDistrictDao.saveBatch(districts);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRegionPkg(Integer id) {
        regionPkgDistrictDao.remove(QueryWrapper.create().eq(RegionPkgDistrict::getRegionPkgId, id));
        return regionPkgDao.removeById(id);
    }

    public RegionPkgDto getRegionPkg(Integer id) {
        RegionPkg regionPkg = regionPkgDao.getById(id);
        if (regionPkg == null) {
            return null;
        }
        RegionPkgDto dto = regionPkgConverter.toRegionPkgDto(regionPkg);
        List<String> codes = regionPkgDistrictDao.list(QueryWrapper.create().eq(RegionPkgDistrict::getRegionPkgId, id))
                .stream()
                .map(RegionPkgDistrict::getDistrictCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        dto.setDistrictCodes(codes);
        return dto;
    }

    public Page<RegionPkg> pageListRegionPkg(RegionPkgQueryDto queryDto) {
        return regionPkgDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .like(RegionPkg::getName, queryDto.getName())
                        .eq(RegionPkg::getType, queryDto.getType()));
    }
}
