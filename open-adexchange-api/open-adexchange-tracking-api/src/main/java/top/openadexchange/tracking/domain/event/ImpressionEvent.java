package top.openadexchange.tracking.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 曝光事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpressionEvent {

    /**
     * 事件唯一ID
     */
    private String eventId;

    /**
     * 曝光ID
     */
    private String impId;

    /**
     * 请求ID
     */
    private String reqId;

    /**
     * 媒体ID
     */
    private String publisherId;

    /**
     * 广告位ID
     */
    private String adSlotId;

    /**
     * 创意ID
     */
    private String crid;

    /**
     * 广告主ID
     */
    private String advId;

    /**
     * DSP ID
     */
    private String dspId;

    /**
     * 结算价格
     */
    private String price;

    /**
     * 价格模式: CPM/CPC
     */
    private String priceMode;

    /**
     * User Agent
     */
    private String ua;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * IPv6
     */
    private String ipv6;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 系统版本
     */
    private String osv;

    /**
     * 事件时间戳
     */
    private long eventTime;
    /**
     * 事件类型
     */
    private EventType eventType;
}
