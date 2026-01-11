package top.openadexchange.openapi.ssp.infra.httpclient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.domain.gateway.OaxHttpClient;
import top.openadexchange.openapi.ssp.domain.model.OaxHttpResponse;

@Extension(keys = {"default", "jdk"})
public class JdkHttpClient implements OaxHttpClient {

    @Resource
    private HttpClient httpClient;

    @Override
    public OaxHttpResponse post(String url, Duration timeout, byte[] body) {
        try {
            HttpResponse<byte[]> response =
                    httpClient.send(HttpRequest.newBuilder().build(), BodyHandlers.ofByteArray());
            return new OaxHttpResponse(response.statusCode(), response.body());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
