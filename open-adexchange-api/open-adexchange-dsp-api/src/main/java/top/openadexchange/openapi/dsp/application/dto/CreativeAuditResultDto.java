package top.openadexchange.openapi.dsp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创意审核结果")
public class CreativeAuditResultDto {

    @Schema(description = "dsp平台素材ID")
    private String creativeId;
    @Schema(description = "审核结果")
    private AuditResult auditResult;
}
