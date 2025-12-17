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
 *  实体类。
 *
 * @author weiping
 * @since 2025-12-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("advertiser")
public class Advertiser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 系统用户ID
     */
    private Long userId;

    /**
     * 代理商ID
     */
    private Long agencyId;

    /**
     * 广告主显示名称
     */
    private String advertiserName;

    /**
     * 公司全称
     */
    private String companyName;

    private String companyType;

    private String country;

    /**
     * 统一社会信用代码
     */
    private String businessLicenseNo;

    /**
     * 法人姓名
     */
    private String legalPersonName;

    private String registeredAddress;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    /**
     * 营业执照
     */
    private String businessLicenseUrl;

    /**
     * 法人身份证
     */
    private String legalPersonIdUrl;

    /**
     * PENDING,APPROVED,REJECTED
     */
    private String auditStatus;

    private String auditReason;

    private LocalDateTime auditTime;

    private Long auditorId;

    /**
     * 1=启用 0=禁用
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
