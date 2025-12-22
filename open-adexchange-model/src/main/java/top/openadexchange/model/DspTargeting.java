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
 * @since 2025-12-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dsp_targeting")
public class DspTargeting implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * dsp ID
     */
    private Long dspId;

    /**
     * 定向操作系统，json数组格式
     */
    private String os;

    /**
     * 国家/地区
     */
    private String country;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 区域定向，JSON数组
     */
    private String region;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
