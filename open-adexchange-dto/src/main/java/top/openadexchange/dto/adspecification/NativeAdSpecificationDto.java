package top.openadexchange.dto.adspecification;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "原生广告位规格配置")
public class NativeAdSpecificationDto {

    private List<NativeAssetDto> nativeAssets;
}
