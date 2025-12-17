package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Native模板字段定义")
public class NativeAssetDto {

    @Schema(description = "资产ID")
    private Long id;

    /**
     * 所属广告位id
     */
    @Schema(description = "所属广告位id")
    private Long adPlacementId;

    /**
     * 字段名称，展示使用
     */
    @Schema(description = "字段名称，展示使用")
    private String assetName;

    /**
     * 字段标识，如 title/main_image
     */
    @Schema(description = "字段标识，如 title/main_image")
    private String assetKey;

    /**
     * TEXT/IMAGE/VIDEO/NUMBER/CTA
     */
    @Schema(description = "资产类型, TEXT/IMAGE/VIDEO/NUMBER/CTA")
    private String assetType;

    /**
     * 是否必填
     */
    @Schema(description = "是否必填")
    private Boolean required;

    /**
     * 是否可重复
     */
    @Schema(description = "是否可重复")
    private Boolean repeatable;

    /**
     * 文本最大长度
     */
    @Schema(description = "文本最大长度")
    private Integer maxLength;

    /**
     * 文本最小长度
     */
    @Schema(description = "文本最小长度")
    private Integer minLength;

    /**
     * 图片/视频宽
     */
    @Schema(description = "图片/视频宽")
    private Integer width;

    /**
     * 图片/视频高
     */
    @Schema(description = "图片/视频高")
    private Integer height;

    /**
     * 比例限制
     */
    @Schema(description = "比例限制")
    private String ratio;

    /**
     * image/jpeg,image/png
     */
    @Schema(description = "MIME类型, 如image/jpeg,image/png")
    private String mimeTypes;

    /**
     * 最大大小(KB)
     */
    @Schema(description = "最大大小(KB)")
    private Integer maxSizeKb;

    @Schema(description = "状态, 1-启用, 0-禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}