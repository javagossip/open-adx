package top.openadexchange.constants.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public enum NativeAssetType {
    TITLE("title", "标题", "title", null, "text"),
    ICON("image", "图标", "icon", null, "image"),
    MAIN_IMAGE("image", "主图", "main_image", null, "image"),
    IMAGE("image", "图片", "image", null, "image"),
    VIDEO("video", "视频", "video", null, "video"),
    DESC("data", "描述", "desc", "desc", "text"),
    DESC2("data", "描述2", "desc2", "desc2", "text"),
    SPONSORED("data", "赞助方", "sponsored", "sponsored", "text"),
    //    DESC("text", "描述", "desc",null,"text"),
    CTA("data", "CTA", "ctatext", "ctatext", "text"),
    RATING("data", "评分", "rating", "rating", "number"),
    LIKES("data", "点赞数", "likes", "likes", "number"),
    PRICE("data", "价格", "price", "price", "money"),
    SALE_PRICE("data", "销售价格", "saleprice", "saleprice", "money"),
    DOWNLOADS("data", "下载数", "downloads", "downloads", "number"),
    PHONE("data", "电话", "phone", "phone", "phone"),
    ADDRESS("data", "地址", "address", "address", "text"),
    DISPLAY_URL("url", "展示地址", "displayurl", "displayurl", "url");

    //title,image,video,data
    private final String assetType;
    /**
     * <pre>
     * reference openrtb native data asset types:
     * https://iabtechlab.com/wp-content/uploads/2016/07/OpenRTB-Native-Ads-Specification-Final-1.2.pdf
     * 7.4 Data Asset Types
     * 1、sponsored
     * 2、dest
     * 3、rating
     * 4、likes
     * 5、downloads
     * 6、price
     * 7、saleprice
     * 8、phone
     * 9、address
     * 10、desc2
     * 11、displayurl
     * 12、ctatext
     * </pre>
     */
    private final String dataAssetType;
    //值类型：
    // text(约束：{ "length": 50, "regex": "^[a-zA-Z0-9 ]+$" }),
    // number(约束：{ "min": 0, "max": 5, "scale": 1 }),
    // money(约束：{ "currency": "USD", "precision",2}),
    // image(约束：{ "w": 100, "h": 500, "wmin": 100, "hmin": 500, "mimes": "image/jpeg"}),
    // video(约束：{ "minDuration": 5, "maxDuration": 30, mimes: "" }),
    private final String format;
    private final String name;
    private final String code;

    NativeAssetType(String assetType, String name, String code, String dataAssetType, String format) {
        this.assetType = assetType;
        this.dataAssetType = dataAssetType;
        this.format = format;
        this.name = name;
        this.code = code;
    }

    public static NativeAssetType from(String assetType) {
        for (NativeAssetType value : values()) {
            if (value.assetType.equalsIgnoreCase(assetType)) {
                return value;
            }
        }
        return null;
    }

    // 获取所有数据资产类型
    public static List<NativeAssetType> getDataAssetTypes() {
        return Arrays.stream(values()).filter(value -> value.dataAssetType != null).collect(Collectors.toList());
    }
}
