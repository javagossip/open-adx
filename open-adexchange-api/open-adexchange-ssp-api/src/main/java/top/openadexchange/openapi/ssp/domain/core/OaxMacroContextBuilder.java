package top.openadexchange.openapi.ssp.domain.core;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.domain.core.AdExchangeEngine.DspBid;
import top.openadexchange.openapi.ssp.spi.MacroContextBuilder;
import top.openadexchange.openapi.ssp.spi.WinPriceCodec;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

import java.util.HashMap;
import java.util.Map;

@Extension(keys = {"default"})
public class OaxMacroContextBuilder implements MacroContextBuilder {

    @Override
    public MacroContext build(DspBid dspBid) {
        WinPriceCodec winPriceCodec = OaxSpiFactory.getWinPriceCodec(dspBid.getDsp().getDspId());

        Map<String, String> valueMap = new HashMap<>();
        valueMap.put(OaxMacros.AUCTION_PRICE, winPriceCodec.encode(dspBid.getPrice(), dspBid.getDsp()));
        valueMap.put(OaxMacros.AUCTION_PLAIN_PRICE, String.valueOf(dspBid.getPrice()));
        return new MacroContext(valueMap);
    }
}
