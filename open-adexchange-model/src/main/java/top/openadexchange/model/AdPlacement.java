package top.openadexchange.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;

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
@Table("ad_placement")
public class AdPlacement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String name;
    private String code;
    private Long userId;
    /**
     * banner,interstitial,native,video,rewarded,audio
     */
    private String adFormat;

    private Integer width;
    private Integer height;
    private Integer minDuration;
    private Integer maxDuration;
    private Integer skipMin;
    private Integer skipAfter;
    private Boolean skippable;
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<String> mimes;
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
