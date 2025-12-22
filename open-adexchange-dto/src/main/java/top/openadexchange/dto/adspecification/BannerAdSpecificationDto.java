package top.openadexchange.dto.adspecification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//Banner广告位规格配置
@Data
public class BannerAdSpecificationDto {

    @Schema(description = "宽")
    private int width;
    @Schema(description = "高")
    private int height;
}
