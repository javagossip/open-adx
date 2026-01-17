package top.openadexchange.openapi.ssp.domain.core;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;

@Component
@Slf4j
public class DspClient {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BidResponse bidding(DspAggregate dspAggregate, BidRequest request) {
        String dspId = dspAggregate.getDsp().getDspId();
        // 1. 获取协议转换扩展点
        RtbProtocolConverter rtbProtocolConverter = OaxSpiFactory.getRtbProtocolConverter(dspId);
        //2. 获取协议调用扩展点
        RtbProtocolInvoker invoker = OaxSpiFactory.getRtbProtocolInvoker(dspId);
        //3. 发起rtb请求调用
        if (request.getTest()) {
            log.info("BidRequest: {}", request);
        }
        Object dspRequest = rtbProtocolConverter.to(dspAggregate.getDsp(), request);
        Object response = invoker.invoke(dspAggregate.getDsp(), dspRequest);
        BidResponse bidResponse = rtbProtocolConverter.from(dspAggregate.getDsp(), request, response);
        if (request.getTest()) {
            log.info("dsp {} BidResponse: {}", dspAggregate.getDsp().getName(), bidResponse.toString());
        }
        return bidResponse;
    }
}
