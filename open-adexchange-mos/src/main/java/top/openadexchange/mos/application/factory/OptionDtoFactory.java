package top.openadexchange.mos.application.factory;

import java.util.List;

import top.openadexchange.dto.OptionDto;
import top.openadexchange.model.SysDict;

public class OptionDtoFactory {

    public static OptionDto fromSysDictData(SysDict sysDictData) {
        OptionDto optionDto = new OptionDto();
        optionDto.setLabel(sysDictData.getDictLabel());
        optionDto.setValue(sysDictData.getDictValue());
        return optionDto;
    }

    public static List<OptionDto> fromSysDictDataList(List<SysDict> sysDictDataList) {
        return sysDictDataList.stream().map(OptionDtoFactory::fromSysDictData).toList();
    }
}
