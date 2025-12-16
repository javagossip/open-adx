package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "媒体广告位查询条件")
public class SiteAdPlacementQueryDto {

    @Schema(description = "站点ID")
    private Long siteId;
    
    @Schema(description = "广告位ID")
    private Long adPlacementId;
    
    @Schema(description = "状态, 1-使用中, 0-禁用")
    private Integer status;
    
    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;
    
    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;
}