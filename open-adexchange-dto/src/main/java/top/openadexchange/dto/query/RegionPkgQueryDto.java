package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "地域包查询条件")
public class RegionPkgQueryDto {

    @Schema(description = "地域包名称（模糊）")
    private String name;

    @Schema(description = "类型：1-地域分级专用，2-通用")
    private Integer type;

    @Schema(description = "页码，默认1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数，默认20")
    private Integer pageSize = 20;
}
