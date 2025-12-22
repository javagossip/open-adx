package top.openadexchange.commons.crypto;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityKeyUtils {

    private static final int KEY_BYTE_LENGTH = 32;

    public static String generateSecurityKey(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];

        // 生成强随机字节
        secureRandom.nextBytes(randomBytes);
        // 使用 RFC 4648 的 URL 安全 Base64 编码，并移除末尾的 '='
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String getApiToken() {
        return generateSecurityKey();
    }

    public static String generateSecurityKey() {
        return generateSecurityKey(KEY_BYTE_LENGTH);
    }

    public static void main(String[] args) {
        System.out.println(generateSecurityKey());
    }
}
