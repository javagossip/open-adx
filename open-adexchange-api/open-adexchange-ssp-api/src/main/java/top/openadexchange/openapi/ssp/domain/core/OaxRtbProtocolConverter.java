package top.openadexchange.openapi.ssp.domain.core;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;

@Extension(keys = {"default"})
public class OaxRtbProtocolConverter implements RtbProtocolConverter<BidRequest.Builder, BidResponse.Builder> {

    @Override
    public BidRequest.Builder to(Dsp dsp, BidRequest.Builder bidRequest) {
        return bidRequest;
    }

    @Override
    public BidResponse.Builder from(Dsp dsp, BidRequest bidRequest, BidResponse.Builder bidResponse) {
        return bidResponse;
    }
}
