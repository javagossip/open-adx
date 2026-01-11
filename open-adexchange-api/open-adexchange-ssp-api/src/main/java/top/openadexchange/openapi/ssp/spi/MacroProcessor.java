package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.openapi.ssp.spi.model.MacroContext;

//广告交易平台宏定义处理
@ExtensionPoint
public interface MacroProcessor {

    String process(String template, MacroContext macroContext);
}
