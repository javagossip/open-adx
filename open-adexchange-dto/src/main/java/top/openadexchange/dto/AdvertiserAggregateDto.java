package top.openadexchange.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告主详情")
public class AdvertiserAggregateDto {

    @Schema(description = "广告主基本信息")
    private AdvertiserDto advertiser;
    @Schema(description = "广告主行业资质信息")
    private List<AdvertiserIndustryLicenseDto> industryLicenses;
    @Schema(description = "广告主审核信息")
    private AdvertiserAuditDto advertiserAudit;
}
