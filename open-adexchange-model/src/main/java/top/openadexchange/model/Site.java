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
 * 实体类。
 *
 * @author weiping
 * @since 2025-12-14
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

    private String appBundle;
    /**
     * 1=website, 2=app
     */
    private Integer siteType;

    private Integer status;
    //ios, android, web
    private String platform;
    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
