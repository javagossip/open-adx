package top.openadexchange.openapi.ssp.domain.core;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;

@Extension(keys = {"default"})
public class OaxRtbProtocolConverter implements RtbProtocolConverter<BidRequest, BidResponse> {

    @Override
    public BidRequest to(BidRequest bidRequest) {
        return bidRequest;
    }

    @Override
    public BidResponse from(BidResponse bidResponse) {
        return bidResponse;
    }
}
