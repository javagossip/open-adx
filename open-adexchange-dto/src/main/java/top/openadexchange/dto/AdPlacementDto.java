package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.openadexchange.constants.enums.AdFormat;
import top.openadexchange.dto.adspecification.AudioAdSpecificationDto;
import top.openadexchange.dto.adspecification.BannerAdSpecificationDto;
import top.openadexchange.dto.adspecification.NativeAdSpecificationDto;
import top.openadexchange.dto.adspecification.VideoAdSpecificationDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "广告位信息")
public class AdPlacementDto {

    @Schema(description = "广告位ID")
    private Long id;
    @Schema(description = "广告位名称")
    private String name;
    /**
     * BANNER,VIDEO,AUDIO,NATIVE
     */
    @Schema(description = "广告位格式, 参见：AdFormat枚举")
    private AdFormat adFormat;
    @Schema(description = "banner广告位配置, RTB规范中定义banner广告位")
    private BannerAdSpecificationDto banner;
    @Schema(description = "视频广告位配置")
    private VideoAdSpecificationDto video;
    @Schema(description = "音频广告位配置")
    private AudioAdSpecificationDto audio;
    @Schema(description = "原生广告位配置")
    private NativeAdSpecificationDto nativeAd;
    @Schema(description = "广告位状态, 1-正常, 0-禁用")
    private Integer status;
}
