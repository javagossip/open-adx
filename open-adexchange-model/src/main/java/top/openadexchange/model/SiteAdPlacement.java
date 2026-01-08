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
 * 媒体广告位管理 实体类。
 *
 * @author weiping
 * @since 2025-12-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("site_ad_placement")
public class SiteAdPlacement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 站点/app id
     */
    private Long siteId;
    private Long userId;
    /**
     * 广告位id
     */
    private Integer adPlacementId;
    private String code;
    /**
     * 平台，ios,android,web
     */
    private String platform;
    /**
     * site广告位名称
     */
    private String name;

    /**
     * 广告位截图url
     */
    private String demoUrl;

    /**
     * 1-使用中, 0-禁用
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
