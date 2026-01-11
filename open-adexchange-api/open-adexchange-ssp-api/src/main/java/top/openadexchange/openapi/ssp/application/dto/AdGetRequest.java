package top.openadexchange.openapi.ssp.application.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 竞价请求对象DTO
 */
@Data
public class AdGetRequest {

    private String id; // ssp广告请求id, 由ssp自动生成
    private List<Imp> imp; // 曝光对象，一次请求可包含多个imp
    private Site site; // 站点对象, 网站流量使用
    private App app; // 移动应用，移动app流量使用
    private Device device; // 设备对象
    private Boolean debug;
    private Boolean test; //是否测试流量
    private Map<String, String> ext; // 扩展字段

    /**
     * 曝光对象DTO
     */
    @Data
    public static class Imp {

        private String id; // ssp自动生成, imp唯一标识
        private String tagid; // 媒体广告位id，由Ad-Exchange定义和分配
        private Map<String, String> ext;
    }

    /**
     * App对象DTO
     */
    @Data
    public static class App {

        private String ver; // App版本
        private Content content; // App内容相关
        private Map<String, String> ext;
    }

    /**
     * 站点对象DTO
     */
    @Data
    public static class Site {

        private Content content; // 网站内容
    }

    /**
     * 内容对象DTO
     */
    @Data
    public static class Content {

        private String title; // 广告展示上下文相关内容标题
        private String keywords; // 广告展示上下文相关内容关键字
    }

    /**
     * 设备对象DTO
     */
    @Data
    public static class Device {

        private String ua; // 设备user-agent
        private Geo geo; // 地理位置对象
        private String ip; // 设备ip
        private String ipv6; // ipv6地址
        private Integer deviceType; // 设备类型, 1-phone,2-pad,3-pc,4-tv
        private String make; // 设备制造商
        private String model; // 设备型号, 如：iPhone
        private String os; // 操作系统，如：ios/Android
        private String osv; // 操作系统版本
        private String carrier; // 运营商, 0-未知,1-移动,2-联通,3-电信
        private Integer connectionType; // 网络连接类型, 1-wifi; 2-2G;3-3G;4-4G;5-5G; 0-未知
        private String ifa; // 明文设备码，如安卓的imei或ios的idfa
        private String didmd5; // md5设备码
        private String mac; // mac地址明文
        private String macmd5; // md5 mac地址
        private String adid; // 安卓id
        private Integer h; // 设备屏幕高
        private Integer w; // 设备屏幕宽
    }

    /**
     * 地理位置对象DTO
     */
    @Data
    public static class Geo {

        private Float lat; // 纬度
        private Float lon; // 经度
    }

    public List<String> getImpTagIds() {
        return imp == null ? Collections.emptyList() : imp.stream().map(Imp::getTagid).toList();
    }

    public String getTagIdByImpId(String impid) {
        if (imp == null || imp.isEmpty()) {
            return null;
        }
        for (Imp imp : imp) {
            if (imp.getId().equals(impid)) {
                return imp.getTagid();
            }
        }
        return null;
    }

    public boolean isTest() {
        return Boolean.TRUE.equals(test);
    }

    public boolean isDebug() {
        return Boolean.TRUE.equals(debug);
    }
}