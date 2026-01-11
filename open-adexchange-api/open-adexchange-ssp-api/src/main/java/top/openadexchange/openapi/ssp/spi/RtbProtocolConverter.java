package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;

//非标准协议的 dsp 协议转换扩展点, 将标准协议转换为非标准协议以及将非标准协议转换为 adx标准协议
@ExtensionPoint
public interface RtbProtocolConverter<REQ, RSP> {

    REQ to(BidRequest bidRequest);

    BidResponse from(RSP rsp);
}
