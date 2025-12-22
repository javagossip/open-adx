package top.openadexchange.openapi.dsp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

@Data
@Schema(description = "广告主行业资质")
public class AdvertiserIndustryLicenseDto {

    @Schema(description = "行业代码（可选），资质可以打包上传这种情况下行业代码不传即可",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String industryCode;
    @Schema(description = "资质名称（可选）",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String licenseName;
    @Schema(description = "资质地址",
            requiredMode = RequiredMode.REQUIRED)
    private String licenseUrl;
}
