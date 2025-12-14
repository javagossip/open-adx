package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author weiping
 * @since 2025-12-14
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
    private Long id;

    /**
     * 站点/app id
     */
    private Long siteId;

    /**
     * 广告位id
     */
    private Long adPlacementId;

}
