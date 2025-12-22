package top.openadexchange.openapi.dsp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "审核结果")
public class AuditResult {

    @Schema(description = "审核状态,参见AuditStatus枚举： " + "PENDING:待审核,APPROVED:审核通过,REJECTED:审核拒绝")
    private String auditStatus;
    @Schema(description = "审核结果, 审核不通过的原因")
    private String auditReason;
    @Schema(description = "审核时间")
    private Long auditTime;
}
