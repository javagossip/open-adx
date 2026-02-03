package top.openadexchange.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点击事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickEvent {

    /**
     * 事件唯一ID
     */
    private String eventId;

    /**
     * 点击ID
     */
    private String clickId;

    /**
     * 曝光ID (关联曝光事件)
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
    private long price;

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
}
