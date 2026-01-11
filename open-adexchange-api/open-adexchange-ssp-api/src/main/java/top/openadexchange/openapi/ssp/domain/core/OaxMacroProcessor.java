package top.openadexchange.openapi.ssp.domain.core;

import java.util.regex.Matcher;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

@Extension(keys = {"default"})
public class OaxMacroProcessor implements MacroProcessor {

    @Override
    public String process(String template, MacroContext context) {
        if (template == null || !template.contains(OaxMacros.START_MACRO)) {
            return template;
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = OaxMacros.MACRO_PATTERN.matcher(template);
        while (matcher.find()) {
            String macroName = matcher.group(1);
            String replacement = context.getMacroValue(macroName);
            if (replacement != null) {
                matcher.appendReplacement(result, replacement);
            } else {
                matcher.appendReplacement(result, matcher.group(0));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
