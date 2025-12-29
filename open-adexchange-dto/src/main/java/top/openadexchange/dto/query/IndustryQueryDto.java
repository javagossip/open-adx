package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "行业查询条件")
public class IndustryQueryDto {

    @Schema(description = "行业编码")
    private String code;

    @Schema(description = "行业名称")
    private String name;

    @Schema(description = "风险等级：0-普通，1-敏感，2-高风险")
    private Integer riskLevel;

    @Schema(description = "是否需要资质：0-否，1-是")
    private Integer needLicense;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;
}