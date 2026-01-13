package top.openadexchange.openapi.ssp.infra.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;
import top.openadexchange.commons.exception.OaxException;
import top.openadexchange.openapi.ssp.domain.gateway.OaxHttpClient;
import top.openadexchange.openapi.ssp.domain.model.OaxHttpResponse;

@Extension(keys = {"default", "jdk"})
public class JdkHttpClient implements OaxHttpClient {

    @Resource
    private HttpClient httpClient;

    @Override
    public OaxHttpResponse post(Map<String, String> headers, String url, Duration timeout, byte[] body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            builder.uri(URI.create(url)).timeout(timeout).POST(HttpRequest.BodyPublishers.ofByteArray(body));

            if (headers != null && !headers.isEmpty()) {
                headers.forEach((k, v) -> builder.header(k, v));
            }
            HttpResponse<byte[]> response = httpClient.send(builder.build(), BodyHandlers.ofByteArray());
            return new OaxHttpResponse(response.statusCode(), response.body());
        } catch (IOException ex) {
            throw new OaxException(ex);
        } catch (InterruptedException ex) {
            throw new OaxException(ex);
        }
    }
}
