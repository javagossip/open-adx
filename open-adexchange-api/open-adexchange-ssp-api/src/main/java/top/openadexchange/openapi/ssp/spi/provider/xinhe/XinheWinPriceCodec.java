package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.commons.crypto.AESECBUtils;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.spi.WinPriceCodec;

@Extension(keys = {"xinhe"})
public class XinheWinPriceCodec implements WinPriceCodec {

    @Override
    public String encode(long price, Dsp dsp) {
        return AESECBUtils.encrypt(String.valueOf(price), dsp.getEncryptionKey());
    }

    @Override
    public long decode(String price, Dsp dsp) {
        return Long.parseLong(AESECBUtils.decrypt(price, dsp.getEncryptionKey()));
    }
}
