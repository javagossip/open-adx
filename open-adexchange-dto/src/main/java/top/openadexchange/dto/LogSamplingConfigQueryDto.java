package top.openadexchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "日志采样配置查询条件")
public class LogSamplingConfigQueryDto {

    @Schema(description = "日志类型：GLOBAL,BID_REQ,BID_RSP,DSP_REQ,DSP_RSP")
    private String logType;

    @Schema(description = "媒体ID")
    private Integer mediaId;

    @Schema(description = "DSP平台ID")
    private Integer dspId;

    @Schema(description = "媒体广告位ID")
    private Integer adSlotId;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "分页页码")
    private int pageNo = 1;

    @Schema(description = "分页大小")
    private int pageSize = 20;
}

