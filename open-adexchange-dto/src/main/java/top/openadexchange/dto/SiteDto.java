package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "站点/app信息")
public class SiteDto {
    @Schema(description = "站点/app id")
    private Long id;
    @Schema(description = "媒体/发布者id")
    private Long publisherId;
    @Schema(description = "站点/app名称")
    private String name;
    @Schema(description = "站点/app域名")
    private String domain;
    @Schema(description = "app id")
    private String appId;
    @Schema(description = "app bundle")
    private String appBundle;

    /**
     * 1=website, 2=app
     */
    @Schema(description = "站点/app类型, 1-网站, 2-app")
    private Integer siteType;
    @Schema(description = "站点/app状态, 1-启用, 0-禁用")
    private Integer status;
}
