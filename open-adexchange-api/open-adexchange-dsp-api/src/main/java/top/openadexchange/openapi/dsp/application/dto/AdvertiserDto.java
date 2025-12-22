package top.openadexchange.openapi.dsp.application.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "广告主信息",
        description = "广告主信息")
public class AdvertiserDto {

    /**
     * 广告主显示名称
     */
    @Schema(description = "广告主显示名称")
    private String advertiserName;

    /**
     * 公司全称
     */
    @Schema(description = "公司全称")
    private String companyName;
    @Schema(description = "行业代码")
    private String industryCode;
    /**
     * 统一社会信用代码
     */
    @Schema(description = "统一社会信用代码")
    private String businessLicenseNo;

    /**
     * 法人姓名
     */
    @Schema(description = "法人姓名")
    private String legalPersonName;
    @Schema(description = "注册地址")
    private String registeredAddress;
    @Schema(description = "联系人名称")
    private String contactName;
    @Schema(description = "联系人电话")
    private String contactPhone;
    @Schema(description = "联系人邮箱")
    private String contactEmail;

    /**
     * 营业执照
     */
    @Schema(description = "营业执照")
    private String businessLicenseUrl;

    @Schema(description = "审核状态, PENDING-审核中, APPROVED-审核通过, REJECTED-审核拒绝")
    private String auditStatus;
    @Schema(description = "审核驳回原因")
    private String auditReason;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    /**
     * 法人身份证
     */
    @Schema(description = "法人身份证")
    private String legalPersonIdUrl;
}
