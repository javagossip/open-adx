package top.openadexchange.dto.query;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.openadexchange.constants.Constants;

/**
 * 报表查询条件DTO
 */
@Data
@Schema(description = "报表查询条件")
public class ReportQueryDto {

    @Schema(description = "媒体ID(可选)")
    private Long publisherId;

    @Schema(description = "媒体名称(可选,模糊匹配)")
    private String publisherName;

    @Schema(description = "站点/APP ID(可选)")
    private Long siteId;

    @Schema(description = "开始日期(yyyyMMdd格式)")
    private Integer startDate;

    @Schema(description = "结束日期(yyyyMMdd格式)")
    private Integer endDate;

    @Schema(description = "页码, 默认1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数, 默认20")
    private Integer pageSize = 20;

    public Integer getPageNo() {
        return pageNo == null ? 1 : pageNo;
    }

    public Integer getPageSize() {
        return pageSize == null ? 20 : pageSize;
    }

    public Integer getStartDate() {
        return startDate == null ? currentDay() : startDate;
    }

    public Integer getEndDate() {
        return endDate == null ? currentDay() : endDate;
    }

    private Integer currentDay() {
        return Integer.parseInt(LocalDate.now().format(Constants.REDIS_KEY_DATEFORMAT));
    }
}
