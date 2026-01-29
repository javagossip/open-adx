package top.openadexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 追踪上下文信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackToken {

    /**
     * 请求 ID
     */
    private String reqId;

    /**
     * 曝光ID
     */
    private String impId;

    /**
     * 媒体 ID
     */
    private String publisherId;

    /**
     * 广告位ID, 对应site_ad_placement表中的广告位编码字段(code)
     */
    private String adSotId;

    /**
     * 创意 ID
     */
    private String crid;

    /**
     * 广告主 ID
     */
    private String advId;

    /**
     * dsp ID, 对应dsp表中的code字段
     */
    private String dspId;

    /**
     * 时间戳
     */
    private long ts;
    /**
     * 过期时间戳，需要根据广告形式不同赋值，比如Banner,Video和 Native的过期时间各不同
     */
    private long expireAt;
    /**
     * User Agent
     */
    private String ua;

    /**
     * ip地址
     */
    private String ip;

    /**
     * ipv6
     */
    private String ipv6;

    /**
     * 结算价格
     */
    private String price;
    /**
     * 操作系统：ios/android
     */
    private String os;
    /**
     * 系统版本
     */
    private String osv;
    /**
     * 结算价格模式:CPM/CPC
     */
    private String priceMode;
    /**
     * 扩展字段
     */
    private Map<String, String> ext;
}
