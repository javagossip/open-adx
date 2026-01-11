package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.model.Dsp;

// RTB协议调用接口扩展点，非标准协议的 dsp需要实现此扩展点，按照 dsp特定协议发起 rtb请求
@ExtensionPoint
public interface RtbProtocolInvoker<REQ, RSP> {

    RSP invoke(Dsp dsp, REQ request);
}
