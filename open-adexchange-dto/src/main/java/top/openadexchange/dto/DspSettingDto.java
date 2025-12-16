package top.openadexchange.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DSP设置")
public class DspSettingDto {

    @Schema(description = "dsp关联的广告位id列表")
    private List<Long> siteAdPlacementIds;
    @Schema(description = "dsp QPS限制")
    private Integer qpsLimit;
    @Schema(description = "dsp定向配置")
    private DspTargetingDto targeting;

    @Data
    public static class DspTargetingDto {

        @Schema(description = "操作系统, ios,android")
        private List<String> osList;
        @Schema(description = "设备类型, phone,pad,pc")
        private List<String> deviceTypes;
        @Schema(description = "地域, 需要先选择国家/地区")
        private List<String> regions;
        @Schema(description = "国家/地区, 中国,美国,日本")
        private String country;
    }
}
