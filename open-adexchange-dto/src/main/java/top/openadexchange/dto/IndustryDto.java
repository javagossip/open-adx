package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "行业信息")
public class IndustryDto {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "行业编码（内部使用，如 FINANCE, GAME, EDUCATION）")
    private String code;

    @Schema(description = "行业名称")
    private String name;

    @Schema(description = "父行业ID，支持多级行业树")
    private Long parentId;

    @Schema(description = "行业层级，从1开始")
    private Integer level;

    @Schema(description = "风险等级：0-普通，1-敏感，2-高风险")
    private Integer riskLevel;

    @Schema(description = "是否需要资质：0-否，1-是")
    private Integer needLicense;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "行业说明")
    private String description;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}