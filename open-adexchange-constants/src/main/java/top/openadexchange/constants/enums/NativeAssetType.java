package top.openadexchange.constants.enums;

import lombok.Getter;

@Getter
public enum NativeAssetType {

    // Text
    TITLE("标题", "title", "text"),
    DESCRIPTION("描述", "description", "text"),
    CTA("CTA", "cta", "text"),
    ADVERTISER("广告商", "advertiser", "text"),
    DISCLAIMER("免责声明", "disclaimer", "text"),

    // Image
    MAIN_IMAGE("主图", "main_image", "image"),
    ICON("图标", "icon", "image"),
    IMAGE("图片", "image", "image"),
    COVER_IMAGE("封面图", "cover_image", "image"),

    // Video
    VIDEO("视频", "video", "video"),
    VIDEO_COVER("视频封面", "video_cover", "video"),

    // Data
    RATING("评分", "rating", "data"),
    DOWNLOADS("下载次数", "downloads", "data"),
    PRICE("价格", "price", "data"),
    SALE_PRICE("原价", "sale_price", "data"),

    // Action
    CLICK_URL("点击链接", "click_url", "action"),
    DEEPLINK("深度链接", "deeplink", "action"),
    FALLBACK_URL("回退链接", "fallback_url", "action"),

    // Compliance
    AD_BADGE("广告标志", "ad_badge", "action"),
    AD_CHOICES("广告选择", "ad_choices", "action"),
    PRIVACY_URL("隐私链接", "privacy_url", "action");

    private final String name;
    private final String code;
    private final String category;

    NativeAssetType(String name, String code, String category) {
        this.name = name;
        this.code = code;
        this.category = category;
    }
}
