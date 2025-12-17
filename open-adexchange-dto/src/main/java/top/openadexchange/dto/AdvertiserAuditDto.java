package top.openadexchange.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告主审核信息")
public class AdvertiserAuditDto {

    @Schema(description = "广告主ID")
    private Long advertiserId;
    @Schema(description = "审核状态, PENDING-审核中, APPROVED-审核通过, REJECTED-审核拒绝")
    private String auditStatus;
    @Schema(description = "审核驳回原因")
    private String auditReason;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
}
