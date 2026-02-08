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
 * Dsp统计表 实体类。
 *
 * @author mac
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dsp_stat")
public class DspStat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * dsp ID
     */
    private Integer dspId;

    /**
     * dsp 编码
     */
    private String dspCode;
    /**
     * dsp 名称
     */
    private String dspName;
    /**
     * 曝光量
     */
    private Long impCount;

    /**
     * 点击量
     */
    private Long clkCount;

    /**
     * 参与竞价数
     */
    private Long bidCount;

    /**
     * 竞价成功数
     */
    private Long winCount;
    private Long reqCount;

    private Long cost;
    /**
     * 统计日期,yyyyMMdd 按天统计
     */
    private Integer statDate;

    /**
     * 创建日期
     */
    private LocalDateTime createTime;

    /**
     * 更新日期
     */
    private LocalDateTime updateTime;

}
