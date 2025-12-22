package top.openadexchange.openapi.dsp.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import top.openadexchange.openapi.dsp.application.dto.CreativeAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.CreativeDto;

@Service
public class CreativeService {

    public String addCreative(CreativeDto creativeDto) {
        return null;
    }

    public Boolean updateCreative(CreativeDto creativeDto) {
        return null;
    }

    public CreativeAuditResultDto getCreativeAuditStatus(String creativeId) {
        return null;
    }

    public List<CreativeAuditResultDto> getCreativeAuditStatusList(List<String> creativeIds) {
        return null;
    }
}
