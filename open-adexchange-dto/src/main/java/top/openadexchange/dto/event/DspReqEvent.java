package top.openadexchange.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DspReqEvent {

    private String dspId;
    private String adSlotId;
    private String siteId;
    private String publisherId;
    private long eventTime;
}
