package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "媒体/发布商查询条件")
public class PublisherQueryDto {

    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型, 1-媒体, 2-发布商")
    private Integer type;
    @Schema(description = "状态, 1-正常, 0-禁用")
    private Integer status;
    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;
    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;
}
