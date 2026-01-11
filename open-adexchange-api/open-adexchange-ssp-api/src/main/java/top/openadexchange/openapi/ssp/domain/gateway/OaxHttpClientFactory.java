package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class OaxHttpClientFactory {

    private OaxHttpClient oaxHttpClient;

    @PostConstruct
    public void init() {
        oaxHttpClient = ExtensionRegistry.getExtensionByKey(OaxHttpClient.class, "default");
    }

    public OaxHttpClient getOaxHttpClient() {
        return oaxHttpClient;
    }
}
