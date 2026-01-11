package top.openadexchange.openapi.ssp.infra.httpclient;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofMillis(50)) // 严格限制建连时间
                .version(Version.HTTP_1_1)
                .build();
    }

    //下面可以配置 ReactorNetty's HttpClient
}
