package top.openadexchange.openapi.ssp.application.service;

import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import top.openadexchange.domain.entity.SiteAdPlacementAggregate;
import top.openadexchange.model.Site;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogType;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse;
import top.openadexchange.openapi.ssp.application.factory.AdGetResponseBuilder;
import top.openadexchange.openapi.ssp.application.factory.BidRequestBuilder;
import top.openadexchange.openapi.ssp.domain.core.AdExchangeEngine;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.utils.LogSamplingUtils;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;

/**
 * 广告获取服务 处理来自媒体方的广告请求
 */
@Service
@Slf4j
public class AdFetchService {

    @Resource
    private AdExchangeEngine adExchangeEngine;
    @Resource
    private BidRequestBuilder bidRequestBuilder;
    @Resource
    private AdGetResponseBuilder adGetResponseBuilder;
    @Resource
    private LogSamplingConfigService lscService;

    /**
     * 获取广告
     *
     * @param request 广告请求对象
     * @return 广告响应对象
     */
    public AdGetResponse fetchAd(AdGetRequest request) {
        // 验证请求参数
        validateRequest(request);
        BidRequest.Builder bidRequest = bidRequestBuilder.buildBidRequest(request);
        if (bidRequest.getDebug() || bidRequest.getTest() || lscService.shouldLog(LogType.MEDIA_REQ, request)) {
            log.info("AdGetRequest:{}", JSON.toJSONString(request));
        }
        Map<String, Bid.Builder> bids = adExchangeEngine.bidding(bidRequest);
        AdGetResponse adGetResponse = adGetResponseBuilder.buildAdGetResponse(bidRequest, request, bids);
        if (bidRequest.getDebug() || bidRequest.getTest() || lscService.shouldLog(LogType.MEDIA_RSP, request)) {
            log.info("AdGetResponse:{}", JSON.toJSONString(adGetResponse));
        }
        return adGetResponse;
    }

    /**
     * 验证请求参数
     *
     * @param request 广告请求对象
     */
    private void validateRequest(AdGetRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("广告请求对象不能为空");
        }
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("请求ID不能为空");
        }
        if (request.getImp() == null || request.getImp().isEmpty()) {
            throw new IllegalArgumentException("至少需要一个曝光对象");
        }
    }

    /**
     * 生成竞价响应ID
     *
     * @return 竞价响应ID
     */
    private String generateBidId() {
        // 生成唯一ID的逻辑
        return "bid-" + System.currentTimeMillis();
    }
}