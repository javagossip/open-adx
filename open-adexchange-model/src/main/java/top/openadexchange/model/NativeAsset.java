package top.openadexchange.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Native模板字段定义 实体类。
 *
 * @author weiping
 * @since 2025-12-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("native_asset")
public class NativeAsset implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 所属广告位id
     */
    private Long adPlacementId;

    /**
     * 字段名称，展示使用
     */
    private String assetName;

    /**
     * 字段标识，如 title/main_image
     */
    private String assetKey;

    /**
     * TEXT/IMAGE/VIDEO/NUMBER/CTA
     */
    private String assetType;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否可重复
     */
    private Boolean repeatable;

    /**
     * 文本最大长度
     */
    private Integer maxLength;

    /**
     * 文本最小长度
     */
    private Integer minLength;

    /**
     * 图片/视频宽
     */
    private Integer width;

    /**
     * 图片/视频高
     */
    private Integer height;

    /**
     * 比例限制
     */
    private String ratio;

    /**
     * image/jpeg,image/png
     */
    private String mimeTypes;

    /**
     * 最大大小(KB)
     */
    private Integer maxSizeKb;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
