package top.openadexchange.openapi.dsp.application.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Native广告")
public class NativeAdDto {

    @Schema(description = "native广告模板ID,由广告交易平台提供")
    private String templateCode;
    @Schema(description = "native广告属性，具体属性需要根据模板的不同而变化")
    private Map<String, Object> assets;
}
