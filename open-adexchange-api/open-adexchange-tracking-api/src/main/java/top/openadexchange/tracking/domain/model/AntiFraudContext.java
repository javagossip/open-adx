package top.openadexchange.tracking.domain.model;

import lombok.Data;

@Data
public class AntiFraudContext {

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
     * 设备类型
     */
    private String deviceType;
}
