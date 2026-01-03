package top.openadexchange.openapi.dsp.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class SignatureUtils {

    /**
     * 生成HmacSHA256签名
     */
    public static String generateSignature(String stringToSign, String secretKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(bytes).toUpperCase();
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    /**
     * 构建待签名字符串
     */
    public static String buildStringToSign(Map<String, String[]> parameterMap, Map<String, String> authParams) {
        StringBuilder stringToSign = new StringBuilder();

        // 添加请求参数（按字母顺序排序）
        Map<String, String[]> sortedParams = new TreeMap<>(parameterMap);
        for (Map.Entry<String, String[]> entry : sortedParams.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values.length > 0) {
                stringToSign.append(key).append("=").append(values[0]).append("&");
            }
        }

        // 添加认证参数（按字母顺序排序，排除signature）
        Map<String, String> sortedAuthParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : authParams.entrySet()) {
            if (!"signature".equals(entry.getKey())) {
                sortedAuthParams.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, String> entry : sortedAuthParams.entrySet()) {
            stringToSign.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        // 移除最后的&
        if (stringToSign.length() > 0) {
            stringToSign.deleteCharAt(stringToSign.length() - 1);
        }

        return stringToSign.toString();
    }
}