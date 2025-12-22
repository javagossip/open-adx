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
 * 广告主行业资质
 *
 * @author weiping
 * @since 2025-12-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("advertiser_industry_license")
public class AdvertiserIndustryLicense implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long advertiserId;

    /**
     * 行业编码，如 medical, finance
     */
    private String industryCode;

    /**
     * 资质名称
     */
    private String licenseName;

    private String licenseUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
