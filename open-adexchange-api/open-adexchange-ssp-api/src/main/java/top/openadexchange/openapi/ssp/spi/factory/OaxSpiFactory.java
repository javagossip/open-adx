package top.openadexchange.openapi.ssp.spi.factory;

import com.chaincoretech.epc.ExtensionRegistry;

import org.springframework.stereotype.Component;

import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.openapi.ssp.spi.WinPriceCodec;

@Component
public class OaxSpiFactory {

    public static MacroProcessor getMacroProcessor(String dspId) {
        MacroProcessor macroProcessor = ExtensionRegistry.getExtensionByKey(MacroProcessor.class, dspId);
        return macroProcessor == null
                ? ExtensionRegistry.getExtensionByKey(MacroProcessor.class, "default")
                : macroProcessor;
    }

    public static <Q, P> RtbProtocolConverter<Q, P> getRtbProtocolConverter(String dspId) {
        RtbProtocolConverter<Q, P> rtbProtocolConverter =
                ExtensionRegistry.getExtensionByKey(RtbProtocolConverter.class, dspId);
        return rtbProtocolConverter == null
                ? ExtensionRegistry.getExtensionByKey(RtbProtocolConverter.class, "default")
                : rtbProtocolConverter;
    }

    public static WinPriceCodec getWinPriceCodec(String dspId) {
        WinPriceCodec winPriceCodec = ExtensionRegistry.getExtensionByKey(WinPriceCodec.class, dspId);
        return winPriceCodec == null
                ? ExtensionRegistry.getExtensionByKey(WinPriceCodec.class, "default")
                : winPriceCodec;
    }

    public static <Q, P> RtbProtocolInvoker<Q, P> getRtbProtocolInvoker(String dspId) {
        RtbProtocolInvoker<Q, P> rtbProtocolInvoker =
                ExtensionRegistry.getExtensionByKey(RtbProtocolInvoker.class, dspId);
        return rtbProtocolInvoker == null
                ? ExtensionRegistry.getExtensionByKey(RtbProtocolInvoker.class, "default")
                : rtbProtocolInvoker;
    }
}

