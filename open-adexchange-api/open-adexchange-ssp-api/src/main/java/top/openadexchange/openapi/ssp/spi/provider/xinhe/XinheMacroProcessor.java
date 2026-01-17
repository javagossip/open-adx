package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.MacroUtils;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

@Extension(keys = {"xinhe"})
public class XinheMacroProcessor implements MacroProcessor {

    @Override
    public String process(String template, MacroContext context) {
        if (template == null || !template.contains(XinheMacros.START_MACRO)) {
            return template;
        }
        return MacroUtils.replaceMacros(template, XinheMacros.MACRO_PATTERN, context);
    }
}
