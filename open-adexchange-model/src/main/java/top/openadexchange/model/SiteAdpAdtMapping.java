package top.openadexchange.model;

import java.io.Serial;
import java.io.Serializable;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 媒体广告位-广告模板关联表 实体类。
 *
 * @author mac
 * @since 2026-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("site_adp_adt_mapping")
public class SiteAdpAdtMapping implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增 ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 媒体广告位 id
     */
    private Integer siteAdPlacementId;

    /**
     * 广告位模板 id
     */
    private Integer adPlacementId;

}
