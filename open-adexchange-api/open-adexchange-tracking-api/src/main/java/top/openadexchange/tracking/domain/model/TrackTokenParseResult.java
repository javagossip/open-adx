package top.openadexchange.tracking.domain.model;

import lombok.Data;
import top.openadexchange.dto.TrackToken;

@Data
public class TrackTokenParseResult {

    private TrackToken data;
    private boolean valid;
    private String errorMsg;

    public void error(String errorMsg) {
        this.valid = false;
        this.errorMsg = errorMsg;
    }
}
