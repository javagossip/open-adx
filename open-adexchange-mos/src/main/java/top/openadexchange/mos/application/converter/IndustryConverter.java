package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;
import top.openadexchange.dto.IndustryDto;
import top.openadexchange.model.Industry;

@Component
public class IndustryConverter {

    public Industry from(IndustryDto industryDto) {
        if (industryDto == null) {
            return null;
        }

        Industry industry = new Industry();
        industry.setId(industryDto.getId());
        industry.setCode(industryDto.getCode());
        industry.setName(industryDto.getName());
        industry.setParentId(industryDto.getParentId());
        industry.setLevel(industryDto.getLevel());
        industry.setRiskLevel(industryDto.getRiskLevel());
        industry.setNeedLicense(industryDto.getNeedLicense());
        industry.setStatus(industryDto.getStatus());
        industry.setDescription(industryDto.getDescription());

        return industry;
    }

    public IndustryDto toIndustryDto(Industry industry) {
        if (industry == null) {
            return null;
        }

        IndustryDto industryDto = new IndustryDto();
        industryDto.setId(industry.getId());
        industryDto.setCode(industry.getCode());
        industryDto.setName(industry.getName());
        industryDto.setParentId(industry.getParentId());
        industryDto.setLevel(industry.getLevel());
        industryDto.setRiskLevel(industry.getRiskLevel());
        industryDto.setNeedLicense(industry.getNeedLicense());
        industryDto.setStatus(industry.getStatus());
        industryDto.setDescription(industry.getDescription());
        industryDto.setCreatedAt(industry.getCreatedAt());
        industryDto.setUpdatedAt(industry.getUpdatedAt());

        return industryDto;
    }
}