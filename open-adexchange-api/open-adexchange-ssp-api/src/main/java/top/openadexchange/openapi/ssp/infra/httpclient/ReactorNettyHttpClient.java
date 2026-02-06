package top.openadexchange.openapi.ssp.infra.httpclient;

import java.time.Duration;
import java.util.Map;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.domain.gateway.OaxHttpClient;
import top.openadexchange.openapi.ssp.domain.model.OaxHttpResponse;

@Extension(keys = {"reactor-netty"})
public class ReactorNettyHttpClient implements OaxHttpClient {

    @Override
    public OaxHttpResponse post(Map<String, String> headers, String url, Duration timeout, byte[] body) {
        //TODO implement me
        return null;
    }
}
