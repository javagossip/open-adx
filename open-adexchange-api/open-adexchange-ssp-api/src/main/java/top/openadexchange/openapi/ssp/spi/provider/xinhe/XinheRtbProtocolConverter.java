package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.rtb.proto.OaxRtbProto;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.BidRequest;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.BidResponse;

@Extension(keys = {"xinhe"})
public class XinheRtbProtocolConverter implements RtbProtocolConverter<BidRequest, BidResponse> {

    @Override
    public BidRequest to(OaxRtbProto.BidRequest bidRequest) {
        return null;
    }

    @Override
    public OaxRtbProto.BidResponse from(BidResponse bidResponse) {
        return null;
    }
}
