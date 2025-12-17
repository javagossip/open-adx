package top.openadexchange.model.adspecification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoAdSpecification {

    private int minDuration;
    private int maxDuration;
    private boolean skippable;
    //视频时长超过这个可以跳过
    private int skipMin;
    //视频播放几秒后可以跳过
    private int skipAfter;
    //比例，如 1:1, 16:9
    private String ratio;
}
