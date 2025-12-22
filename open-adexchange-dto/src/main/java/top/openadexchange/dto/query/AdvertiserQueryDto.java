package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "广告主查询条件")
public class AdvertiserQueryDto {

    @Schema(description = "广告主名称")
    private String advertiserName;

    @Schema(description = "状态, 1-启用, 0-禁用")
    private Integer status;

    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;
}