package top.openadexchange.openapi.ssp.spi.factory;

import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;

import top.openadexchange.openapi.ssp.spi.MacroContextBuilder;
import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.openapi.ssp.spi.WinPriceCodec;

@Component
public class OaxSpiFactory {

    private static final String DEFAULT_EXTENSION_KEY = "default";

    public static MacroProcessor getMacroProcessor(String dspId) {
        MacroProcessor macroProcessor = ExtensionRegistry.getExtensionByKey(MacroProcessor.class, dspId);
        return macroProcessor == null
                ? ExtensionRegistry.getExtensionByKey(MacroProcessor.class, DEFAULT_EXTENSION_KEY)
                : macroProcessor;
    }

    public static <Q, P> RtbProtocolConverter<Q, P> getRtbProtocolConverter(String dspId) {
        RtbProtocolConverter<Q, P> rtbProtocolConverter =
                ExtensionRegistry.getExtensionByKey(RtbProtocolConverter.class, dspId);
        return rtbProtocolConverter == null ? ExtensionRegistry.getExtensionByKey(RtbProtocolConverter.class,
                DEFAULT_EXTENSION_KEY) : rtbProtocolConverter;
    }

    public static WinPriceCodec getWinPriceCodec(String dspId) {
        WinPriceCodec winPriceCodec = ExtensionRegistry.getExtensionByKey(WinPriceCodec.class, dspId);
        return winPriceCodec == null
                ? ExtensionRegistry.getExtensionByKey(WinPriceCodec.class, DEFAULT_EXTENSION_KEY)
                : winPriceCodec;
    }

    public static <Q, P> RtbProtocolInvoker<Q, P> getRtbProtocolInvoker(String dspId) {
        RtbProtocolInvoker<Q, P> rtbProtocolInvoker =
                ExtensionRegistry.getExtensionByKey(RtbProtocolInvoker.class, dspId);
        return rtbProtocolInvoker == null ? ExtensionRegistry.getExtensionByKey(RtbProtocolInvoker.class,
                DEFAULT_EXTENSION_KEY) : rtbProtocolInvoker;
    }

    public static MacroContextBuilder getMacroContextBuilder(String dspId) {
        MacroContextBuilder macroContextBuilder = ExtensionRegistry.getExtensionByKey(MacroContextBuilder.class, dspId);
        return macroContextBuilder == null ? ExtensionRegistry.getExtensionByKey(MacroContextBuilder.class,
                DEFAULT_EXTENSION_KEY) : macroContextBuilder;
    }
}

