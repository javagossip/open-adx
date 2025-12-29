package top.openadexchange.dto;

import java.util.List;

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
    @Schema(description = "平台, ios, android, web")
    private String platform;
    @Schema(description = "关键字，多个关键字逗号分隔")
    private String keywords;
    @Schema(description = "内容分类,同一个app/site可以同时属于多个类别")
    private List<String> cats;
    /**
     * 1=website, 2=app
     */
    @Schema(description = "站点/app类型, 1-网站, 2-app")
    private Integer siteType;
    @Schema(description = "站点/app状态, 1-启用, 0-禁用")
    private Integer status;
}
