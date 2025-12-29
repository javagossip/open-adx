package top.openadexchange.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告行业字典表 实体类。
 *
 * @author weipingwang
 * @since 2025-12-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("industry")
public class Industry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 行业编码（内部使用，如 FINANCE, GAME, EDUCATION）
     */
    private String code;

    /**
     * 行业名称
     */
    private String name;

    /**
     * 父行业ID，支持多级行业树
     */
    private Long parentId;

    /**
     * 行业层级，从1开始
     */
    private Integer level;

    /**
     * 风险等级：0-普通，1-敏感，2-高风险
     */
    private Integer riskLevel;

    /**
     * 是否需要资质：0-否，1-是
     */
    private Integer needLicense;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 行业说明
     */
    private String description;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
