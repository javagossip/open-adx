package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.SecurityUtils;

import jakarta.annotation.Resource;

import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.dto.PublisherDto;
import top.openadexchange.model.Publisher;

@Component
public class PublisherConverter {

    @Resource
    protected EntityCodeService entityCodeService;

    public Publisher from(PublisherDto publisherDto) {
        if (publisherDto == null) {
            return null;
        }

        Publisher publisher = new Publisher();
        publisher.setId(publisherDto.getId());
        publisher.setName(publisherDto.getName());
        publisher.setContactEmail(publisherDto.getContactEmail());
        publisher.setContactPhone(publisherDto.getContactPhone());
        publisher.setStatus(publisherDto.getStatus());
        
        // 设置code和userId
        if (entityCodeService != null) {
            publisher.setCode(entityCodeService.generatePublisherCode());
        }
        publisher.setUserId(SecurityUtils.getUserId());
        
        return publisher;
    }

    public PublisherDto toPublisherDto(Publisher publisher) {
        if (publisher == null) {
            return null;
        }

        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(publisher.getId());
        publisherDto.setName(publisher.getName());
        publisherDto.setContactEmail(publisher.getContactEmail());
        publisherDto.setContactPhone(publisher.getContactPhone());
        publisherDto.setStatus(publisher.getStatus());
        
        return publisherDto;
    }
}