package top.openadexchange.openapi.dsp.application.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.commons.AssertUtils;
import top.openadexchange.dao.CreativeDao;
import top.openadexchange.domain.entity.CreativeAggregate;
import top.openadexchange.model.Creative;
import top.openadexchange.openapi.dsp.application.converter.CreativeConverter;
import top.openadexchange.openapi.dsp.application.dto.CreativeAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.CreativeDto;
import top.openadexchange.openapi.dsp.application.validator.CreativeValidator;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;
import top.openadexchange.openapi.dsp.repository.CreativeAggregateRepository;

@Service
public class CreativeService {

    @Resource
    private CreativeDao creativeDao;
    @Resource
    private CreativeValidator creativeValidator;
    @Resource
    private CreativeConverter creativeConverter;
    @Resource
    private CreativeAggregateRepository creativeAggregateRepository;

    public String addCreative(CreativeDto creativeDto) {
        creativeValidator.validateForAddCreative(creativeDto);
        CreativeAggregate creativeAggregate = creativeConverter.from(creativeDto);
        creativeAggregateRepository.saveOrUpdateCreativeAggregate(creativeAggregate);

        return creativeAggregate.getCreative().getCreativeId();
    }

    public Boolean updateCreative(CreativeDto creativeDto) {
        creativeValidator.validateForUpdateCreative(creativeDto);
        CreativeAggregate creativeAggregate = creativeConverter.from(creativeDto);
        creativeAggregateRepository.saveOrUpdateCreativeAggregate(creativeAggregate);

        return true;
    }

    public CreativeAuditResultDto getCreativeAuditStatus(String creativeId) {
        AssertUtils.notBlank(creativeId, ApiErrorCode.DSP_CREATIVE_ID_IS_REQUIRED);
        Creative creative = creativeDao.getOne(QueryWrapper.create().eq(Creative::getDspCreativeId, creativeId));
        return creativeConverter.toCreativeAuditResultDto(creative);
    }

    public List<CreativeAuditResultDto> getCreativeAuditStatusList(List<String> creativeIds) {
        AssertUtils.notEmpty(creativeIds, ApiErrorCode.DSP_CREATIVE_ID_IS_REQUIRED);
        List<Creative> creatives = creativeDao.getCreativesByDspCreativeIds(creativeIds);
        return creatives.stream()
                .map(creative -> creativeConverter.toCreativeAuditResultDto(creative))
                .filter(Objects::nonNull)
                .toList();
    }
}
