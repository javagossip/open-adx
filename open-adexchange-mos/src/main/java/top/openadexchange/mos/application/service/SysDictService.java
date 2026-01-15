package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.DistrictDao;
import top.openadexchange.dao.RegionPkgDao;
import top.openadexchange.dao.SysDictDataDao;
import top.openadexchange.dto.OptionDto;
import top.openadexchange.model.District;
import top.openadexchange.model.RegionPkg;
import top.openadexchange.model.SysDict;
import top.openadexchange.mos.application.factory.OptionDtoFactory;

import static top.openadexchange.model.table.RegionPkgTableDef.*;
import static top.openadexchange.model.table.SysDictTableDef.*;

@Service
public class SysDictService {

    @Resource
    private DistrictDao districtDao;
    @Resource
    private SysDictDataDao sysDictDataDao;
    @Resource
    private RegionPkgDao regionPkgDao;

    public List<District> getDistrictsByParentId(Integer parentId) {
        return districtDao.getDistrictsByParentId(parentId);
    }

    public List<OptionDto> getOptionsByType(String dictType) {
        List<SysDict> sysDictList = sysDictDataDao.list(QueryWrapper.create().eq(SysDict::getDictType, dictType));
        return OptionDtoFactory.fromSysDictDataList(sysDictList);
    }

    public List<OptionDto> getCountries(String searchKey) {
        List<SysDict> sysDictList = sysDictDataDao.list(QueryWrapper.create()
                .where(SYS_DICT.DICT_TYPE.eq("sys_country_code")
                        .and(SYS_DICT.DICT_LABEL.like(searchKey).or(SYS_DICT.DICT_VALUE.like(searchKey)))));

        return OptionDtoFactory.fromSysDictDataList(sysDictList);
    }

    public List<OptionDto> getIndustries(String searchKey) {
        List<SysDict> sysDictList = sysDictDataDao.list(QueryWrapper.create()
                .where(SYS_DICT.DICT_TYPE.eq("sys_ad_industry")
                        .and(SYS_DICT.DICT_LABEL.like(searchKey).or(SYS_DICT.DICT_VALUE.like(searchKey)))));
        return OptionDtoFactory.fromSysDictDataList(sysDictList);
    }

    public List<District> getDistrictsByCodes(List<String> codes) {
        return districtDao.getDistrictsByCodes(codes);
    }

    public List<OptionDto> getRegionLevels() {
        return regionPkgDao.listAs(QueryWrapper.create()
                .select(REGION_PKG.NAME.as("label"), REGION_PKG.ID.as("value"))
                .eq(RegionPkg::getType, 1), OptionDto.class);
    }
}
