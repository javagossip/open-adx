package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.openadexchange.constants.enums.Platform;

@Data
@Schema(description = "站点/app查询条件")
public class SiteQueryDto {

    @Schema(description = "站点/app名称")
    private String name;
    @Schema(description = "站点/app类型, 1-网站, 2-app")
    private Integer type;
    @Schema(description = "站点/app状态, 1-启用, 0-禁用")
    private Integer status;
    @Schema(description = "媒体/发布者id")
    private Long publisherId;
    @Schema(description = "平台, 参见Platform枚举, IOS,ANDROID,WEB")
    private Platform platform;
    @Schema(description = "页码")
    private Integer pageNo = 1;
    @Schema(description = "每页条数")
    private Integer pageSize = 20;
}
