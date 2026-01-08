package top.openadexchange.openapi.ssp.application.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 广告响应对象
 */
@Data
public class AdFetchResponse {
    private String id; // 竞价请求id, 和BidRequest的id保持一致
    private List<SeatBidDto> seatbid; // 针对单个广告位的竞价响应
    private String bidid; // dsp生成的竞价响应id

    /**
     * 原生广告DTO
     */
    @Data
    public static class NativeAdDto {
        private String templateId; // 原生广告模板id，一个原生广告位可能支持多个广告模版
        private Map<String, String> assets; // 资产映射
    }

    /**
     * 座位竞价DTO
     */
    @Data
    public static class SeatBidDto {
        private List<BidDto> bid; // 同一个广告位的多个出价
        private String seat; // 广告位名称

        /**
         * 竞价DTO
         */
        @Data
        public static class BidDto {
            private String id; // dsp平台参与出价id
            private String impid; // 曝光id, 关联竞价请求中Imp中id
            private Float price; // 广告出价, 单位是分
            private String crid; // dsp平台创意id
            private String dealid; // 请求pmp dealid
            private String nurl; // 如果不填写则不会发送winNotice通知
            private List<String> impTrackers; // 曝光监测
            private List<String> clkTrackers; // 点击监测
            private String ldp; // 广告落地页
            private String creativeUrl; // 广告创意地址
            private Integer clickType; // 点击类型：1-浏览器打开  2-安卓应用下载  3-deeplink，4-ios应用
            private String bundle; // 安卓应用包名或者ios的appid， 点击类型为安卓应用下载和ios应用时候，bundle必填
            private NativeAdDto nativeAd; // 原生广告响应
            private String appDownloadUrl; // 应用下载地址
            private String appName; // 应用名称
            private String deeplink; // deeplink链接
        }
    }
}