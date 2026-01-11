package top.openadexchange.openapi.ssp.spi.model;

import java.util.Map;

public class MacroContext {

    private final Map<String, String> macroValueMap;

    public MacroContext(Map<String, String> macroValueMap) {
        this.macroValueMap = macroValueMap;
    }

    public String getMacroValue(String macroName) {
        return macroValueMap == null ? null : macroValueMap.get(macroName);
    }
}
