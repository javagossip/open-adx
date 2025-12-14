package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Date;
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

    private String licenseNo;

    private String licenseUrl;

    private String issuedBy;

    private Date validFrom;

    private Date validTo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
