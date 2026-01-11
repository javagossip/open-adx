package top.openadexchange.openapi.ssp.application.service;

import org.springframework.stereotype.Component;

import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;

@Component
public class DspClient {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BidResponse bidding(DspAggregate dsp, BidRequest request) {
        String dspId = dsp.getDsp().getDspId();
        // 1. 获取协议转换扩展点
        RtbProtocolConverter rtbProtocolConverter = OaxSpiFactory.getRtbProtocolConverter(dspId);
        //2. 获取协议调用扩展点
        RtbProtocolInvoker invoker = OaxSpiFactory.getRtbProtocolInvoker(dspId);
        //3. 发起rtb请求调用
        Object response = invoker.invoke(dsp.getDsp(), rtbProtocolConverter.to(request));
        BidResponse bidResponse = rtbProtocolConverter.from(response);

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
