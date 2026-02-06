package top.openadexchange.openapi.ssp.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Publisher;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.factory.BidRequestBuilder;
import top.openadexchange.openapi.ssp.application.factory.IndexKeysBuilder;
import top.openadexchange.openapi.ssp.application.service.DiagnosticService;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/diagnostics")
@Tag(name = "广告引擎诊断")
public class DiagnosticController {

    @Resource
    private IndexKeysBuilder indexKeysBuilder;
    @Resource
    private BidRequestBuilder bidRequestBuilder;
    @Resource
    private DiagnosticService diagnosticService;

    @PostMapping("/index-keys")
    @Operation(summary = "根据广告请求构建索引key")
    public ApiResponse<IndexKeys> buildIndexKeys(@RequestBody AdGetRequest adGetRequest) {
        return ApiResponse.success(indexKeysBuilder.buildIndexKeys(bidRequestBuilder.buildBidRequest(adGetRequest)));
    }

    @GetMapping("/cache-data/inspect")
    @Operation(summary = "检查缓存数据")
    public ApiResponse<Object> inspectCacheData(String key, int cacheType) {
        return ApiResponse.success(diagnosticService.inspectCacheData(key, cacheType));
    }

    @PostMapping("/match-dsps")
    @Operation(summary = "根据广告请求匹配对应的dsp")
    public ApiResponse<List<Dsp>> matchDsps(@RequestBody AdGetRequest adGetRequest) {
        return ApiResponse.success(diagnosticService.matchDsps(adGetRequest));
    }
}
