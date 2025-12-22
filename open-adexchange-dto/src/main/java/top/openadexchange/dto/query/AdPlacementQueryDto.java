package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "广告位查询条件")
public class AdPlacementQueryDto {

    @Schema(description = "广告位名称")
    private String name;
    
    @Schema(description = "广告位编码")
    private String code;
    
    @Schema(description = "广告位格式, banner,interstitial,native,video,rewarded,audio")
    private String adFormat;
    
    @Schema(description = "广告位状态, 1-正常, 0-禁用")
    private Integer status;
    
    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;
    
    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;
}