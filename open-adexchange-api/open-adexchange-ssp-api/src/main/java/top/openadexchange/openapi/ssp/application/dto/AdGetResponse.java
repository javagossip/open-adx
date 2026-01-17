package top.openadexchange.openapi.ssp.application.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 广告响应对象
 */
@Data
public class AdGetResponse {

    private String id; // 请求id, 和BidRequest的id保持一致
    private List<Ad> ads; // 针对单个广告位的竞价响应

    /**
     * 原生广告DTO
     */
    @Data
    public static class NativeAd {

        private String title;
        private String icon;
        private String desc;
        private String main_image;
        private List<String> images;
        private String video;
        private String ctaText;
        private String rating;
        private String likes;
        private String downloads;
        private String sponsored;
        private String price;
        private String salePrice;
        private String phone;
        private String address;
        private String desc2;
        private String displayUrl;
        private Map<String, String> ext;
    }

    /**
     * 广告信息，每个广告位对应一个广告
     */
    @Data
    public static class Ad {

        private String impid; // 曝光id, 关联竞价请求中Imp中id
        private String tagid; //广告位 id
        private String crid; // dsp平台创意id
        private List<String> pm; // 曝光监测
        private List<String> cm; // 点击监测
        private String ldp; // 广告落地页
        private String curl; // 广告创意地址
        private int ct; // clickType-点击类型：1-浏览器打开  2-安卓应用下载  3-deeplink，4-ios应用
        private String bundle; // 安卓应用包名或者ios的appid， 点击类型为安卓应用下载和ios应用时候，bundle必填
        private String adl; // 应用下载地址
        private String dlk; // deeplink链接
        private NativeAd nativeAd; // 原生广告响应
    }
}