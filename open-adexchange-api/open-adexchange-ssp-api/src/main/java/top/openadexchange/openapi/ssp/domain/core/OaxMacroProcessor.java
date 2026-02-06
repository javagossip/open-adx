package top.openadexchange.openapi.ssp.domain.core;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.MacroUtils;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

@Extension(keys = {"default"})
public class OaxMacroProcessor implements MacroProcessor {

    @Override
    public String process(String template, MacroContext context) {
        if (template == null || !template.contains(OaxMacros.START_MACRO)) {
            return template;
        }
        return MacroUtils.replaceMacros(template, OaxMacros.MACRO_PATTERN, context);
    }
}
