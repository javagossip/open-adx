package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.model.Dsp;

@ExtensionPoint
public interface RtbProtocolInvoker<REQ, RSP> {

    RSP invoke(Dsp dsp, REQ request);
}
