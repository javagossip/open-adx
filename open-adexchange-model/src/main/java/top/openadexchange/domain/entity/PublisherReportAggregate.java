package top.openadexchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 媒体报表聚合查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherReportAggregate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 媒体ID
     */
    private Long publisherId;

    /**
     * 媒体名称
     */
    private String publisherName;

    /**
     * 曝光量
     */
    private Long impCount;

    /**
     * 点击量
     */
    private Long clickCount;

    /**
     * 点击率(%)
     */
    private BigDecimal clickRate;

    /**
     * 媒体收入(元)
     */
    private Long revenue;
    /**
     * adx平台收入(元)
     */
    private Long adxRevenue;
}
