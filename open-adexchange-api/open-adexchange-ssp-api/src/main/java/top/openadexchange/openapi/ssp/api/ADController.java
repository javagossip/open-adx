package top.openadexchange.openapi.ssp.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;

import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse;
import top.openadexchange.openapi.ssp.application.service.AdFetchService;

/**
 * 广告获取接口控制器 用于媒体方获取广告
 */
@RestController
@RequestMapping("/v1/ads")
@Slf4j
public class ADController {

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
        try {
            AdGetResponse adGetResponse = adFetchService.fetchAd(request);
            if (adGetResponse == null) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            return adGetResponse;
        } catch (Exception ex) {
            log.error("拉取广告失败", ex);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        return null;
    }
}