package top.openadexchange.openapi.ssp.domain.gateway;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.openapi.ssp.domain.model.OaxHttpResponse;

import java.time.Duration;
import java.util.Map;

//oax http 客户端接口定义，支持后续扩展提供不同的 http 客户端实现
@ExtensionPoint
public interface OaxHttpClient {

    OaxHttpResponse post(Map<String, String> headers, String url, Duration timeout, byte[] body);

    //    void setHeader(String key, String value);
}
