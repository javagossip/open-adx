package top.openadexchange.openapi.ssp.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.factory.BidRequestBuilder;
import top.openadexchange.openapi.ssp.application.factory.IndexKeysBuilder;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;

@RestController
@RequestMapping("/v1/diagnostics")
public class DiagnosticController {

    @Resource
    private IndexKeysBuilder indexKeysBuilder;
    @Resource
    private BidRequestBuilder bidRequestBuilder;

    @PostMapping("/index-keys")
    @Operation(summary = "根据广告请求构建索引key")
    public IndexKeys buildIndexKeys(AdGetRequest adGetRequest) {
        return indexKeysBuilder.buildIndexKeys(bidRequestBuilder.buildBidRequest(adGetRequest));
    }
}
