package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.ExtensionRegistry;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class OaxHttpClientFactory {

    private OaxHttpClient oaxHttpClient;

    public OaxHttpClient getOaxHttpClient() {
        if (oaxHttpClient == null) {
            oaxHttpClient = ExtensionRegistry.getExtensionByKey(OaxHttpClient.class, "default");
        }
        return oaxHttpClient;
    }
}
