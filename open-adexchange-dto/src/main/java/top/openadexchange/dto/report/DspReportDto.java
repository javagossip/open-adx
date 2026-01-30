package top.openadexchange.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DSP报表")
public class DspReportDto {

    private String dspId;
    private String dspCode;
    private String dspName;
    private Long impCount;
    private Long clkCount;
    private Long bidCount;
    private Long winCount;
    private int statDate;
}
