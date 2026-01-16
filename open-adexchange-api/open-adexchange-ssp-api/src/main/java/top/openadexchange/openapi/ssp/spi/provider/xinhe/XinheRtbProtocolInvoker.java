package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import com.chaincoretech.epc.annotation.Extension;

import com.google.protobuf.InvalidProtocolBufferException;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.domain.gateway.OaxHttpClient;
import top.openadexchange.openapi.ssp.domain.gateway.OaxHttpClientFactory;
import top.openadexchange.openapi.ssp.domain.model.OaxHttpResponse;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.BidRequest;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.BidResponse;

import java.time.Duration;
import java.util.Map;

@Extension(keys = {"xinhe"})
@Slf4j
public class XinheRtbProtocolInvoker implements RtbProtocolInvoker<BidRequest, BidResponse> {

    @Resource
    private OaxHttpClientFactory oaxHttpClientFactory;

    @Override
    public BidResponse invoke(Dsp dsp, BidRequest request) {
        if (request.getTest()) {
            log.info("xinhe's BidRequest: {}", request.toString());
        }
        OaxHttpClient httpClient = oaxHttpClientFactory.getOaxHttpClient();
        try {
            OaxHttpResponse response = httpClient.post(Map.of("Content-Type", "application/protobuf"),
                    dsp.getBidEndpoint(),
                    Duration.ofMillis(dsp.getTimeoutMs()),
                    request.toByteArray());
            if (response.getStatusCode() != 200) {
                return null;
            }
            BidResponse bidResponse = BidResponse.parseFrom(response.getBody());
            if (request.getTest()) {
                log.info("xinhe's BidResponse: {}", bidResponse.toString());
            }
            return bidResponse;
        } catch (InvalidProtocolBufferException ex) {
            log.error("parse xinhe bid response error", ex);
            return null;
        } catch (Exception ex) {
            log.error("invoke xinhe dsp error", ex);
            return null;
        }
    }
}
