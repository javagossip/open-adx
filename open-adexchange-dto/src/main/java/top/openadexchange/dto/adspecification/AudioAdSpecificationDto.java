package top.openadexchange.dto.adspecification;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "音频广告位配置")
public class AudioAdSpecificationDto {

    @Schema(description = "最小时长")
    private int minDuration;
    @Schema(description = "最大时长")
    private int maxDuration;
    private boolean skippable;
    @Schema(description = "播放几秒后跳过")
    private int skipAfter;
    //音频总时长要大于skipMin
    @Schema(description = "跳过最小时长")
    private int skipMin;
    @Schema(description = "支持的音频格式")
    private List<String> mimes;
}
