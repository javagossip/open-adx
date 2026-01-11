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

        private String templateId; // 原生广告模板id，一个原生广告位可能支持多个广告模版
        private Map<String, String> assets; //原生广告属性

        private String title;
        private String mainImage;
        private String icon;
        private List<String> images;
        private List<String> videos;
        private String cta;
        private String desc;
        private String desc2;
        private String sponsored; //赞助方
        private String rating; //评分
        private String likes; //点赞数
        private String price; //价格
        private String salePrice; //销售价格
        private String downloads; //下载数
        private String phone; //电话
        private String address; //地址
        private String displayUrl; //展示地址
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