package top.openadexchange.openapi.ssp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//Log Sampling Config Context
@Data
@AllArgsConstructor
public class LscContext {

    private int mediaId;
    private int siteId;
    private int adSlotId;
}
