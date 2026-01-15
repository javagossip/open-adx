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
 * @author mac
 * @since 2026-01-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("floor_price")
public class FloorPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 媒体广告位 id
     */
    private Integer siteAdPlacementId;

    /**
     * 广告位底价
     */
    private Long floorPrice;

    /**
     * 行业 id
     */
    private Integer industryId;

    private Integer regionLevelId;

    /**
     * 0-禁用,1-启用
     */
    private Integer status;

}
