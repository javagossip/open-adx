package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DSP查询条件")
public class DspQueryDto {

    @Schema(description = "DSP名称")
    private String name;
    
    @Schema(description = "DSP状态, 1-使用中，0-停用")
    private Integer status;
    
    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;
    
    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;
}