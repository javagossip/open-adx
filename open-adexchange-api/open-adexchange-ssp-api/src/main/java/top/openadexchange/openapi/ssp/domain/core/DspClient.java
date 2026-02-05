package top.openadexchange.openapi.ssp.domain.core;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.enums.PriceMode;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.application.factory.TrackTokenBuilder;
import top.openadexchange.openapi.ssp.application.service.RateLimiterManager;
import top.openadexchange.openapi.ssp.config.OaxEngineProperties;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.rtb.proto.OaxRtbProto;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;

@Component
@Slf4j
public class DspClient {

    @Resource
    private OaxEngineProperties oaxEngineProperties;
    @Resource
    private RateLimiterManager rateLimiterManager;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BidResponse.Builder bidding(DspAggregate dspAggregate, BidRequest.Builder request) {
        if (!rateLimiterManager.tryAcquire(dspAggregate.getDspId())) {
            log.warn("dsp {} rate limit", dspAggregate.getDsp().getName());
            return null;
        }
        String dspId = dspAggregate.getDsp().getDspId();
        // 1. 获取协议转换扩展点
        RtbProtocolConverter rtbProtocolConverter = OaxSpiFactory.getRtbProtocolConverter(dspId);
        //2. 获取协议调用扩展点
        RtbProtocolInvoker invoker = OaxSpiFactory.getRtbProtocolInvoker(dspId);
        //3. 发起rtb请求调用
        if (request.getTest() || request.getDebug()) {
            log.info("BidRequest: {}", request);
        }
        Object dspRequest = rtbProtocolConverter.to(dspAggregate.getDsp(), request);
        if (request.getTest() || request.getDebug()) {
            log.info("dsp {} BidRequest: {}", dspAggregate.getDsp().getName(), dspRequest);
        }
        Object response = invoker.invoke(dspAggregate.getDsp(), dspRequest);
        if (request.getTest() || request.getDebug()) {
            log.info("dsp {} BidResponse: {}", dspAggregate.getDsp().getName(), response);
        }
        BidResponse.Builder bidResponse = rtbProtocolConverter.from(dspAggregate.getDsp(), request.build(), response);

        if (request.getTest() || request.getDebug()) {
            log.info("BidResponse: {}", bidResponse.toString());
        }
        return bidResponse;
    }
}
