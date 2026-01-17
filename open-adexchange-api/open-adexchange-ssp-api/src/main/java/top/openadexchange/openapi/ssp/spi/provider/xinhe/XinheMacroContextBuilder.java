package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.domain.core.AdExchangeEngine.DspBid;
import top.openadexchange.openapi.ssp.spi.MacroContextBuilder;
import top.openadexchange.openapi.ssp.spi.WinPriceCodec;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

import java.util.Map;

@Extension(keys = {"xinhe"})
public class XinheMacroContextBuilder implements MacroContextBuilder {

    @Override
    public MacroContext build(DspBid dspBid) {
        WinPriceCodec winPriceCodec = OaxSpiFactory.getWinPriceCodec(dspBid.getDspId());
        String encodedPrice = winPriceCodec.encode(dspBid.getPrice(), dspBid.getDsp());
        return new MacroContext(Map.of(XinheMacros.WIN_PRICE, encodedPrice));
    }
}
