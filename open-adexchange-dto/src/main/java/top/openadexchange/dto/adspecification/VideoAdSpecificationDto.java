package top.openadexchange.dto.adspecification;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//视频广告位规格配置
@Data
@Schema(description = "视频广告位规格配置")
public class VideoAdSpecificationDto {
    @Schema(description = "宽")
    private int width;
    @Schema(description = "高")
    private int height;
    @Schema(description = "最小时长")
    private int minDuration;
    @Schema(description = "最大时长")
    private int maxDuration;
    private boolean skippable;
    @Schema(description = "播放几秒后跳过")
    private int skipAfter;
    //视频总时长要大于skipMin
    @Schema(description = "跳过最小时长")
    private int skipMin;
    @Schema(description = "支持的视频格式")
    private List<String> mimes;
}
