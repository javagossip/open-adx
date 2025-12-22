package top.openadexchange.openapi.dsp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "广告主审核结果")
public class AdvertiserAuditResultDto {

    @Schema(description = "dsp平台广告主ID")
    private String advertiserId;
    @Schema(description = "审核结果")
    private AuditResult auditResult;
}
