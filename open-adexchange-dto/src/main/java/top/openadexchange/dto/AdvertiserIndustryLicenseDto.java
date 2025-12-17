package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告主行业资质信息")
public class AdvertiserIndustryLicenseDto {

    @Schema(description = "资质ID")
    private Long id;

    @Schema(description = "广告主ID")
    private Long advertiserId;

    /**
     * 行业编码，如 medical, finance
     */
    @Schema(description = "行业编码，如 medical, finance")
    private String industryCode;

    /**
     * 资质名称
     */
    @Schema(description = "资质名称")
    private String licenseName;

    @Schema(description = "资质文件URL")
    private String licenseUrl;

}