package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告主信息")
public class AdvertiserDto {

    @Schema(description = "广告主ID")
    private Long id;

    /**
     * 代理商ID
     */
    @Schema(description = "代理商ID")
    private Long agencyId;

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
    @Schema(description = "公司类型")
    private String companyType;
    @Schema(description = "公司所在国家, iso 3166-1 alpha-2国家编码")
    private String country;

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
    /**
     * 1=启用 0=禁用
     */
    @Schema(description = "状态, 1-启用, 0-禁用")
    private Integer status;
}
