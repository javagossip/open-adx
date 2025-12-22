package top.openadexchange.openapi.dsp.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "创意信息",
        description = "创意信息")
public class CreativeDto {

    @Schema(description = "dsp平台创意ID")
    private String creativeId;
    @Schema(description = "dsp平台广告主ID")
    private String advertiserId;
    @Schema(description = "广告创意名称")
    private String name;
    @Schema(description = "创意类型: BANNER,VIDEO,AUDIO,NATIVE")
    private String creativeType;
    @Schema(description = "创意地址")
    private String creativeUrl;
    @Schema(description = "广告落地页地址")
    private String landingPage;
    @Schema(description = "原生/信息流广告数据")
    private NativeAdDto nativeAd;
}
