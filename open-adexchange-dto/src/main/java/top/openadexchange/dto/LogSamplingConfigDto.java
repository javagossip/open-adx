package top.openadexchange.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "日志采样配置")
public class LogSamplingConfigDto {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "日志类型：GLOBAL,BID_REQ,BID_RSP,DSP_REQ,DSP_RSP,MEDIA_REQ,MEDIA_RSP")
    private String logType;

    @Schema(description = "媒体ID")
    private Integer mediaId;
    @Schema(description = "媒体名称")
    private String mediaName;

    @Schema(description = "DSP平台ID")
    private Integer dspId;
    @Schema(description = "DSP平台名称")
    private String dspName;

    @Schema(description = "媒体广告位ID")
    private Integer adSlotId;
    @Schema(description = "媒体广告位名称")
    private String adSlotName;

    @Schema(description = "采样率-万分位，0-10000")
    private Integer samplingRate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
}

