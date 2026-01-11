package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.model.Dsp;

/**
 * 竞价结果价格编码解码器
 */
@ExtensionPoint
public interface WinPriceCodec {

    String encode(long price, Dsp dsp);

    long decode(String price, Dsp dsp);
}
