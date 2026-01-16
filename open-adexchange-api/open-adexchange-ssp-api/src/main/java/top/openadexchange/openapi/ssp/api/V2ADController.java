package top.openadexchange.openapi.ssp.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse;
import top.openadexchange.openapi.ssp.application.service.AdFetchService;

@RestController
@RequestMapping("/v2/ads")
public class V2ADController {

    @Resource
    private AdFetchService adFetchService;

    /**
     * 获取广告接口
     *
     * @param request 广告请求对象
     * @return 广告响应对象
     */
    @PostMapping
    @Operation(summary = "拉取广告")
    public AdGetResponse fetchAd(@RequestBody AdGetRequest request, HttpServletResponse response) {
        AdGetResponse adGetResponse = adFetchService.fetchAd(request);
        if (adGetResponse == null) {
            response.setStatus(204);
        }
        return adGetResponse;
    }
}
