package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.openapi.ssp.domain.core.AdExchangeEngine.DspBid;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;

/**
 * 宏替换上下文构建器，用来构建宏替换上下文，上下文中保存着具体宏变量对应的值
 */
@ExtensionPoint
public interface MacroContextBuilder {

    MacroContext build(DspBid dspBid);
}
