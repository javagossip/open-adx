package top.openadexchange.openapi.ssp.infra.httpclient;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.domain.gateway.OaxHttpClient;
import top.openadexchange.openapi.ssp.domain.model.OaxHttpResponse;

import java.time.Duration;

@Extension(keys = {"reactor-netty"})
public class ReactorNettyHttpClient implements OaxHttpClient {

    @Override
    public OaxHttpResponse post(String url, Duration timeout, byte[] body) {
        return null;
    }
}
