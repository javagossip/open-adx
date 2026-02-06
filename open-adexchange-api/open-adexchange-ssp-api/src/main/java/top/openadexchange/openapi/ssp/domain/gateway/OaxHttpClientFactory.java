package top.openadexchange.openapi.ssp.domain.gateway;

import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

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
