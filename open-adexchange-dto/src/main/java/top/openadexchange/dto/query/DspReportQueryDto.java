package top.openadexchange.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DSP报表查询条件")
public class DspReportQueryDto {

    @Schema(description = "DSP名称(可选,模糊匹配)")
    private String dspName;
    @Schema(description = "DSP编码(精确匹配)")
    private String dspCode;
    @Schema(description = "开始日期(yyyyMMdd格式)")
    private Integer startDate;

    @Schema(description = "结束日期(yyyyMMdd格式)")
    private Integer endDate;
    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;
}
