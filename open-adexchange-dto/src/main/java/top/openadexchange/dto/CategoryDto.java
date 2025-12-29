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
@Schema(description = "分类信息")
public class CategoryDto {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类体系：IAB_V1 / IAB_V2 / INTERNAL")
    private String system;

    @Schema(description = "分类编码，如 IAB1, IAB1-2")
    private String code;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父级分类编码")
    private String parentCode;

    @Schema(description = "分类层级")
    private Integer level;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "分类说明")
    private String description;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}