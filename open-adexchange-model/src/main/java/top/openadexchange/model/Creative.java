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
 * 广告创意表，包括adx 自有创意以及 dsp 平台创意 实体类。
 *
 * @author mac
 * @since 2026-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("creative")
public class Creative implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 广告交易平台创意ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 广告组ID
     */
    private Long adGroupId;

    /**
     * 广告推广计划ID
     */
    private Long campaignId;

    /**
     * 广告主 ID
     */
    private Long advertiserId;

    /**
     * dsp 平台创意ID
     */
    private String dspCreativeId;

    /**
     * dsp 平台广告主ID
     */
    private String dspAdvertiserId;

    /**
     * 创意类型: BANNER/VIDEO/NATIVE/AUDIO
     */
    private String creativeType;

    /**
     * 广告创意地址
     */
    private String creativeUrl;
    /**
     * 素材Mime Type
     */
    private String mimes;
    /**
     * 视频/音频广告时长
     */
    private Integer duration;

    /**
     * 广告宽度
     */
    private Integer width;

    /**
     * 广告高度
     */
    private Integer height;

    /**
     * 创意名称
     */
    private String name;

    /**
     * 点击落地页
     */
    private String landingUrl;

    /**
     * 深度链接
     */
    private String deeplinkUrl;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核拒绝原因
     */
    private String auditReason;
    /**
     * 创意审核时间
     */
    private LocalDateTime auditTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 广告交易平台广告位 ID
     */
    private String adxPlacementId;

    /**
     * adx 广告模版 ID
     */
    private String adxTemplateId;
    /**
     * 创意编码，避免直接使用主键ID
     */
    private String creativeId;

}
