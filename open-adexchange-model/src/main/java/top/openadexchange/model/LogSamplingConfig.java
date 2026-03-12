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
 * @author mac
 * @since 2026-02-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("log_sampling_config")
public class LogSamplingConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 采用日志类型： GLOBAL,BID_REQ,BID_RSP,DSP_REQ,DSP_RSP,MEDIA_REQ,MEDIA_RSP
     */
    private String logType;

    /**
     * 媒体id
     */
    private Integer mediaId;

    /**
     * dsp 平台 id
     */
    private Integer dspId;

    /**
     * 媒体广告位 id
     */
    private Integer adSlotId;

    /**
     * 采样率-万分位
     */
    private Integer samplingRate;

    /**
     * 创建日期
     */
    private LocalDateTime createTime;

    /**
     * 更新日期
     */
    private LocalDateTime updateTime;

    /**
     * 启用/禁用,0-禁用,1-启用
     */
    private Integer status;

}
