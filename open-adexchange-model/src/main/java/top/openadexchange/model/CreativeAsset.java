package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创意素材资产表 实体类。
 *
 * @author mac
 * @since 2026-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("creative_asset")
public class CreativeAsset implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 无业务意义
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long creativeId;

    /**
     * TITLE/IMAGE/VIDEO/AUDIO/DATA
     */
    private String assetType;

    /**
     * 数据资产类型，当asset_type=DATA时需要判断具体的 asset语义类型
     */
    private String dataAssetType;

    /**
     * asset_type=DATA时，字段 format, 选项：TEXT,NUMBER,MONEY
     */
    private String format;

    /**
     * 资产语义key，如 title/main_image/cta_text
     */
    private String assetKey;

    /**
     * 值（URL/文本/JSON）
     */
    private String assetValue;

    /**
     * image/jpeg video/mp4
     */
    private String mimeType;

    /**
     * 宽
     */
    private Integer width;

    /**
     * 高
     */
    private Integer height;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 文件大小KB
     */
    private Integer sizeKb;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 排序
     */
    private Integer sortOrder;

    private LocalDateTime createdAt;

}
