package top.openadexchange.openapi.ssp.api;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest;
import top.openadexchange.openapi.ssp.application.dto.AdFetchResponse;
import top.openadexchange.openapi.ssp.application.service.AdFetchService;

/**
 * 广告获取接口控制器 用于媒体方获取广告
 */
@RestController
@RequestMapping("/v1/ads")
public class AdFetchController {

    @Resource
    private AdFetchService adFetchService;

    /**
     * 获取广告接口
     *
     * @param request 广告请求对象
     * @return 广告响应对象
     */
    @PostMapping("/fetch")
    public AdFetchResponse fetchAd(@RequestBody AdFetchRequest request) {
        return adFetchService.fetchAd(request);
    }
}