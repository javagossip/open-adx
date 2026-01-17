package top.openadexchange.commons.crypto;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import top.openadexchange.commons.exception.OaxException;

public class AESECBUtils {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";

    public static String encrypt(String input, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
        } catch (Exception ex) {
            throw new OaxException(ex);
        }
    }

    public static String decrypt(String encryptedInput, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedInput));
            return new String(decryptedBytes);
        } catch (Exception ex) {
            throw new OaxException(ex);
        }
    }
}
