package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table("ad_placement")
public class AdPlacement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String name;

    /**
     * banner,interstitial,native,video,rewarded,audio
     */
    private String adFormat;

    private Integer width;

    private Integer height;

    /**
     * 广告位配置，不同格式的广告位属性不同
     */
    private String adSpecification;

    /**
     * 广告位底价
     */
    private BigDecimal floorPrice;

    /**
     * 结算币种
     */
    private String currency;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
