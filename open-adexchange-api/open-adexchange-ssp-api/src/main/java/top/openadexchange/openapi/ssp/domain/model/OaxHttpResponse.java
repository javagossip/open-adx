package top.openadexchange.openapi.ssp.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OaxHttpResponse {

    private int statusCode;
    private byte[] body;
}
