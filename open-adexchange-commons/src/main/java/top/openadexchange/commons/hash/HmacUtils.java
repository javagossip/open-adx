package top.openadexchange.commons.hash;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public final class HmacUtils {

    /**
     * 使用 Guava 实现 HMAC-SHA256 加密
     *
     * @param key 密钥
     * @param data 待加密数据
     * @return 加密后的十六进制字符串
     */
    public static String hmacSha256(String key, String data) {
        return Hashing.hmacSha256(key.getBytes(StandardCharsets.UTF_8))
                .hashString(data, StandardCharsets.UTF_8)
                .toString();
    }

    public static String hmacSha256(String key, byte[] data) {
        return Hashing.hmacSha256(key.getBytes(StandardCharsets.UTF_8)).hashBytes(data).toString();
    }
}
