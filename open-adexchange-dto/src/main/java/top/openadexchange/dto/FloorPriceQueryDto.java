package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "底价查询条件")
public class FloorPriceQueryDto {

    @Schema(description = "媒体广告位id")
    private Integer siteAdPlacementId;

    @Schema(description = "行业id")
    private Integer industryId;

    @Schema(description = "地域等级id")
    private Integer regionLevelId;

    @Schema(description = "状态 0-禁用,1-启用")
    private Integer status;

    @Schema(description = "分页页码")
    private int pageNo = 1;

    @Schema(description = "分页大小")
    private int pageSize = 20;
}