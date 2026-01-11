package top.openadexchange.openapi.ssp.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse;
import top.openadexchange.openapi.ssp.application.factory.AdGetResponseBuilder;
import top.openadexchange.openapi.ssp.application.factory.BidRequestBuilder;
import top.openadexchange.openapi.ssp.domain.core.AdExchangeEngine;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;

/**
 * 广告获取服务 处理来自媒体方的广告请求
 */
@Service
public class AdFetchService {

    @Resource
    private OaxEngineServices oaxEngineServices;
    @Resource
    private AdExchangeEngine adExchangeEngine;
    @Resource
    private BidRequestBuilder bidRequestBuilder;
    @Resource
    private AdGetResponseBuilder adGetResponseBuilder;

    /**
     * 获取广告
     *
     * @param request 广告请求对象
     * @return 广告响应对象
     */
    public AdGetResponse fetchAd(AdGetRequest request) {
        // 验证请求参数
        validateRequest(request);
        BidRequest bidRequest = bidRequestBuilder.buildBidRequest(request);
        Map<String, Bid> bids = adExchangeEngine.bidding(bidRequest);
        return adGetResponseBuilder.buildAdGetResponse(request,bids);
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