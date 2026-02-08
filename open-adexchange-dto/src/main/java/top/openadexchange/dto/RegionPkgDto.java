package top.openadexchange.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "地域包信息")
public class RegionPkgDto {

    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "地域包名称")
    private String name;

    @Schema(description = "类型：1-地域分级专用，2-通用")
    private Integer type;

    @Schema(description = "关联的地区编码列表")
    private List<String> districtCodes;
}
