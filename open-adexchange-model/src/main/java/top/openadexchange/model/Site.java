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
 * @author weipingwang
 * @since 2025-12-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("site")
public class Site implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long publisherId;

    private String name;

    private String domain;

    private String appId;

    private String platform;

    private String appBundle;

    /**
     * 1=website, 2=app
     */
    private Integer siteType;

    private Integer status;

    /**
     * 所属用户id
     */
    private Long userId;

    /**
     * 关键字列表，多个字段按 逗号分隔
     */
    private String keywords;

    /**
     * 站点/app 分类
     */
    private String cat;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
