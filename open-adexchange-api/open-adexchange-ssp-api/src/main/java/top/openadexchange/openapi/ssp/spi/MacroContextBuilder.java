package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.openapi.ssp.domain.core.AdExchangeEngine.DspBid;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

@ExtensionPoint
public interface MacroContextBuilder {

    MacroContext build(DspBid dspBid);
}
