package top.openadexchange.openapi.ssp.spi;

import com.chaincoretech.epc.annotation.ExtensionPoint;

/**
 * 竞价结果价格编码解码器
 */
@ExtensionPoint
public interface WinPriceCodec {

    String encode(double price);

    double decode(String price);
}
