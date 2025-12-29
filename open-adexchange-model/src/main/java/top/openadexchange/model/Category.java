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
 * App/Site 内容分类字典表 实体类。
 *
 * @author weipingwang
 * @since 2025-12-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 分类体系：IAB_V1 / IAB_V2 / INTERNAL
     */
    private String system;

    /**
     * 分类编码，如 IAB1, IAB1-2
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父级分类编码
     */
    private String parentCode;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 分类说明
     */
    private String description;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
