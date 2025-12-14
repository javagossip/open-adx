package top.openadexchange.mos.application.converter;

import org.mapstruct.Mapper;

import top.openadexchange.dto.PublisherDto;
import top.openadexchange.model.Publisher;

@Mapper(componentModel = "spring")
public interface PublisherConverter {

    Publisher from(PublisherDto publisherDto);

    PublisherDto toPublisherDto(Publisher publisher);
}
