package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.MacroUtils;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

import java.util.Map;
import java.util.regex.Pattern;

@Extension(keys = {"xinhe"})
public class XinheMacroProcessor implements MacroProcessor {

    @Override
    public String process(String template, MacroContext context) {
        if (template == null || !template.contains(XinheMacros.START_MACRO)) {
            return template;
        }
        String result = template;
        for (Pattern pattern : XinheMacros.MACRO_PATTERNS) {
            result = MacroUtils.replaceMacros(result, pattern, context);
        }
        return result;
    }
}
