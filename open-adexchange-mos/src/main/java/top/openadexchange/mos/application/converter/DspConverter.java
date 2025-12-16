package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.DspDto;
import top.openadexchange.model.Dsp;

@Mapper(componentModel = "spring")
public interface DspConverter {

    Dsp from(DspDto dspDto);

    DspDto toDspDto(Dsp dsp);
}