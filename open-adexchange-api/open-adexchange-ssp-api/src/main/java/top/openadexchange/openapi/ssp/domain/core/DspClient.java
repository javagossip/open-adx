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
    public BidResponse bidding(DspAggregate dsp, BidRequest request) {
        String dspId = dsp.getDsp().getDspId();
        // 1. 获取协议转换扩展点
        RtbProtocolConverter rtbProtocolConverter = OaxSpiFactory.getRtbProtocolConverter(dspId);
        //2. 获取协议调用扩展点
        RtbProtocolInvoker invoker = OaxSpiFactory.getRtbProtocolInvoker(dspId);
        //3. 发起rtb请求调用
        if (request.getTest()) {
            log.info("BidRequest: {}", request.toString());
        }
        Object dspRequest = rtbProtocolConverter.to(dsp.getDsp(), request);
        Object response = invoker.invoke(dsp.getDsp(), dspRequest);
        BidResponse bidResponse = rtbProtocolConverter.from(dsp.getDsp(), request, response);

        if (bidResponse != null) {
            BidResponse.Builder builder = BidResponse.newBuilder(bidResponse);
            builder.getSeatbidBuilderList().forEach(seatBidBuilder -> {
                seatBidBuilder.getBidBuilderList().forEach(bidBuilder -> {
                    bidBuilder.setDspId(dsp.getDsp().getDspId());
                });
            });
            return builder.build();
        }
        return null;
    }
}
