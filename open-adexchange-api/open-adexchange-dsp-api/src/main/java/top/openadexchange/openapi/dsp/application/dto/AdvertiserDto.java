package top.openadexchange.openapi.dsp.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema(name = "广告主信息",
        description = "广告主信息")
public class AdvertiserDto {

    /**
     * 广告主显示名称
     */
    @Schema(description = "广告主名称",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String advertiserName;
    @Schema(description = "DSP平台广告主ID")
    private String advertiserId;
    /**
     * 公司全称
     */
    @Schema(description = "公司全称",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;
    @Schema(description = "行业代码",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String industryCode;
    /**
     * 统一社会信用代码
     */
    @Schema(description = "统一社会信用代码",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessLicenseNo;

    /**
     * 法人姓名
     */
    @Schema(description = "法人姓名",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String legalPersonName;
    @Schema(description = "注册地址",
            requiredMode = Schema.RequiredMode.REQUIRED)
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
    @Schema(description = "营业执照url",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessLicenseUrl;
    /**
     * 法人身份证
     */
    @Schema(description = "法人身份证url",
            requiredMode = RequiredMode.NOT_REQUIRED)
    private String legalPersonIdUrl;
    @Schema(description = "广告主行业资质")
    private List<AdvertiserIndustryLicenseDto> advertiserIndustryLicenses;
}
