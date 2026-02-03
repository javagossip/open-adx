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
 * 媒体广告位数据统计 实体类。
 *
 * @author mac
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ad_slot_stat")
public class AdSlotStat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 媒体广告位编码
     */
    private String adSlotId;

    /**
     * 媒体ID
     */
    private Long publisherId;
    /**
     * 站点/APP ID
     */
    private Long siteId;
    /**
     * 统计日期(yyyyMMdd)
     */
    private Integer statDate;
    private Long reqCount;
    private Long bidCount;
    private Long winCount;
    /**
     * 曝光量
     */
    private Long impCount;

    /**
     * 点击量
     */
    private Long clickCount;
    /**
     * 广告主花费
     */
    private Long dspCost;
    /**
     * 媒体收益
     */
    private Long revenue;
    /**
     * ADX收益
     */
    private Long adxRevenue;
    /**
     * 创建日期
     */
    private LocalDateTime createTime;

    /**
     * 更新日期
     */
    private LocalDateTime updateTime;

}
