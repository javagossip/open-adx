package top.openadexchange.openapi.ssp.domain.core;

import java.time.Duration;
import java.util.Map;

import com.chaincoretech.epc.annotation.Extension;
import com.google.protobuf.InvalidProtocolBufferException;

import jakarta.annotation.Resource;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.domain.gateway.OaxHttpClientFactory;
import top.openadexchange.openapi.ssp.domain.model.OaxHttpResponse;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;

@Extension(keys = {"default"})
public class OaxRtbProtocolInvoker implements RtbProtocolInvoker<BidRequest, BidResponse> {

    @Resource
    private OaxHttpClientFactory oaxHttpClientFactory;

    @Override
    public BidResponse invoke(Dsp dsp, BidRequest request) {
        OaxHttpResponse oaxHttpResponse = oaxHttpClientFactory.getOaxHttpClient()
                .post(Map.of("Content-Type", "application/protobuf"),
                        dsp.getBidEndpoint(),
                        Duration.ofMillis(dsp.getTimeoutMs()),
                        request.toByteArray());
        if (oaxHttpResponse.getStatusCode() == 200) {
            try {
                BidResponse.Builder builder = BidResponse.newBuilder().mergeFrom(oaxHttpResponse.getBody());
                builder.getSeatbidBuilderList().forEach(seatBidBuilder -> {
                    seatBidBuilder.getBidBuilderList().forEach(bidBuilder -> {
                        bidBuilder.setDspId(dsp.getDspId());
                    });
                });
                return builder.build();
            } catch (InvalidProtocolBufferException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
}
