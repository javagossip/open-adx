package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.DistrictDao;
import top.openadexchange.dao.SysDictDataDao;
import top.openadexchange.dto.OptionDto;
import top.openadexchange.model.District;
import top.openadexchange.model.SysDict;

import static top.openadexchange.model.table.SysDictTableDef.*;

@Service
public class SysDictService {

    @Resource
    private DistrictDao districtDao;
    @Resource
    private SysDictDataDao sysDictDataDao;

    public List<District> getDistrictsByParentId(Integer parentId) {
        return districtDao.getDistrictsByParentId(parentId);
    }

    public List<OptionDto> getOptionsByType(String dictType) {
        List<SysDict> sysDictList = sysDictDataDao.list(QueryWrapper.create().eq(SysDict::getDictType, dictType));
        return sysDictList.stream()
                .map(sysDict -> new OptionDto(sysDict.getDictLabel(), sysDict.getDictValue()))
                .toList();
    }

    public List<OptionDto> getCountries(String searchKey) {
        List<SysDict> sysDictList = sysDictDataDao.list(QueryWrapper.create()
                .where(SYS_DICT.DICT_TYPE.eq("sys_country_code")
                        .and(SYS_DICT.DICT_LABEL.like(searchKey).or(SYS_DICT.DICT_VALUE.like(searchKey)))));

        return sysDictList.stream()
                .map(sysDict -> new OptionDto(sysDict.getDictLabel(), sysDict.getDictValue()))
                .toList();
    }
}
