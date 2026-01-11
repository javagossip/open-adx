package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;

@ExtensionPoint
public interface RtbProtocolConverter<REQ, RSP> {

    REQ to(BidRequest bidRequest);

    BidResponse from(RSP rsp);
}
