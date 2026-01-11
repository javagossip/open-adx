package top.openadexchange.openapi.ssp.domain.core;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

@Extension(keys = {"default"})
public class OaxMacroProcessor implements MacroProcessor {

    @Override
    public String process(String template, MacroContext context) {
        return "";
    }
}
