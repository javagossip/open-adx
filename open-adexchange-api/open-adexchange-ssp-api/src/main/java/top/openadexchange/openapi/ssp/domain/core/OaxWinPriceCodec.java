package top.openadexchange.openapi.ssp.domain.core;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

import com.chaincoretech.epc.annotation.Extension;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;

import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.spi.WinPriceCodec;

@Extension(keys = {"default"})
public class OaxWinPriceCodec implements WinPriceCodec {

    private static final int IV_SIZE = 16;
    private static final int PRICE_SIZE = 8;
    private static final int SIG_SIZE = 4;

    @Override
    public String encode(long price, Dsp dsp) {
        byte[] eKey = BaseEncoding.base64Url().decode(dsp.getEncryptionKey());
        byte[] iKey = BaseEncoding.base64Url().decode(dsp.getIntegrationKey());

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        // 2. 使用 Guava 计算 HMAC-SHA1 Pad
        byte[] pad = Hashing.hmacSha1(eKey).hashBytes(iv).asBytes();

        // 3. XOR 异或运算 (价格 8 字节)
        byte[] priceBytes = Longs.toByteArray(price);
        byte[] encPrice = new byte[8];
        for (int i = 0; i < 8; i++) {
            encPrice[i] = (byte) (priceBytes[i] ^ pad[i]);
        }

        // 4. 计算签名 (Integrity)
        byte[] sig = Hashing.hmacSha1(iKey).newHasher().putBytes(priceBytes).putBytes(iv).hash().asBytes();
        // 5. 拼接结果并编码为 WebSafe Base64
        byte[] result = ByteBuffer.allocate(28).put(iv).put(encPrice).put(sig, 0, 4).array();
        return BaseEncoding.base64Url().omitPadding().encode(result);
    }

    @Override
    public long decode(String encryptedPrice, Dsp dsp) {
        byte[] eKey = BaseEncoding.base64Url().decode(dsp.getEncryptionKey());
        byte[] iKey = BaseEncoding.base64Url().decode(dsp.getIntegrationKey());
        // 1. WebSafe Base64 解码 (Guava 提供的 BaseEncoding 会自动处理 URL 安全字符)
        byte[] decoded = BaseEncoding.base64Url().decode(encryptedPrice);

        if (decoded.length != IV_SIZE + PRICE_SIZE + SIG_SIZE) {
            throw new IllegalArgumentException("Invalid encrypted price length.");
        }
        // 2. 拆解字节流
        ByteBuffer buffer = ByteBuffer.wrap(decoded);
        byte[] iv = new byte[IV_SIZE];
        byte[] encPrice = new byte[PRICE_SIZE];
        byte[] sig = new byte[SIG_SIZE];

        buffer.get(iv);
        buffer.get(encPrice);
        buffer.get(sig);

        // 3. 生成 Pad 并通过异或 (XOR) 还原价格
        // Guava 的 Hashing.hmacSha1 是线程安全的
        byte[] pad = Hashing.hmacSha1(eKey).hashBytes(iv).asBytes();

        byte[] priceBytes = new byte[PRICE_SIZE];
        for (int i = 0; i < PRICE_SIZE; i++) {
            priceBytes[i] = (byte) (encPrice[i] ^ pad[i]);
        }

        long price = Longs.fromByteArray(priceBytes);
        // 4. 完整性校验 (Integrity Check)
        // 重新计算签名：HMAC-SHA1(iKey, priceBytes + iv)
        byte[] computedSigFull = Hashing.hmacSha1(iKey).newHasher().putBytes(priceBytes).putBytes(iv).hash().asBytes();

        // 取前 4 位进行对比
        byte[] computedSig = Arrays.copyOf(computedSigFull, SIG_SIZE);

        if (!Arrays.equals(sig, computedSig)) {
            throw new SecurityException("Price integrity check failed! The price may have been tampered with.");
        }
        return price;
    }
}
