package top.openadexchange.openapi.ssp.spi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.openadexchange.openapi.ssp.spi.model.MacroContext;

public final class MacroUtils {

    public static String replaceMacros(String template, Pattern macroPattern, MacroContext context) {
        if (template == null || template.length() == 0) {
            return template;
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = macroPattern.matcher(template);
        while (matcher.find()) {
            String macroName = matcher.group(1);
            String replacement = context.getMacroValue(macroName);
            if (replacement != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } else {
                //因为不确定各个 dsp宏的格式，所以这里需要对不需要替换的宏进行特殊处理，使用引号进行转义
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
