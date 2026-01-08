package top.openadexchange.dto.adspecification;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.openadexchange.constants.enums.NativeAssetType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Native模板字段定义")
public class NativeAssetDto {

    @Schema(description = "AssetID")
    private Integer id;

    /**
     * 所属广告位id
     */
    @Schema(description = "所属广告位id")
    private Integer adPlacementId;

    /**
     * 字段名称，展示使用
     */
    @Schema(description = "字段名称，展示使用",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetName;

    /**
     * 字段标识，如 title/main_image
     */
    @Schema(description = "字段标识，如 title/main_image,业务逻辑中使用",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetKey;

    /**
     * TEXT/IMAGE/VIDEO/NUMBER/CTA
     */
    @Schema(description = "资产类型, TITLE,IMAGE,VIDEO,DATA",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetType;
    /**
     * 数据资产类型, 具体参见：{@link NativeAssetType#getDataAssetTypes()}
     */
    @Schema(description = "数据资产类型, 具体参见：{@link NativeAssetType#getDataAssetTypes()}")
    private String dataAssetType;
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
    @Schema(description = "MIME类型, 如image/jpeg,image/png",
            requiredMode = RequiredMode.NOT_REQUIRED)
    private String mimeTypes;

    /**
     * 最大大小(KB)
     */
    @Schema(description = "最大大小(KB)",
            requiredMode = RequiredMode.NOT_REQUIRED)
    private Integer maxSizeKb;
    /**
     * 数据资产字段约束，不同的数据资产类型约束不同，具体约束参见： text(约束：{ "length": 50, "regex": "^[a-zA-Z0-9 ]+$" }), number(约束：{ "min": 0,
     * "max": 5, "scale": 1 }), money(约束：{ "currency": "USD", "precision",2})
     */
    @Schema(description = """
                          数据资产字段约束，不同的数据资产类型约束不同，具体约束参见：
                          <pre>
                          text(约束：{ "minLength": 50, "maxLength": 100}),
                          number(约束：{ "min": 0, "max": 5, "scale": 1 }),
                          money(约束：{ "currency": "币种", "precision",2})
                          </pre>
                          """)
    private Map<String, Object> constraints;
}