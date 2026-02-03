package top.openadexchange.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DspBidEvent {

    private String dspId;
    private String adSlotId;
    private String crid;
    private String advId;
    private long eventTime;
}
