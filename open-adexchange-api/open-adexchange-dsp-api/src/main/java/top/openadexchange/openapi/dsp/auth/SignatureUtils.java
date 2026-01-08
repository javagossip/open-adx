package top.openadexchange.openapi.dsp.auth;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SignatureUtils {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 生成HmacSHA256签名
     */
    public static String generateSignature(String stringToSign, String secretKey) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
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

    public static String buildStringToSign(Map<String, String[]> parameterMap,
            Map<String, String> authParams,
            String requestBody) {
        // 1. 定义一个用于存放所有待签名键值对的列表
        List<String> paramPairs = new ArrayList<>();

        // 2. 加入 Header 中的基础认证参数
        paramPairs.add("clientId=" + authParams.get("clientId"));
        paramPairs.add("timestamp=" + authParams.get("timestamp"));
        paramPairs.add("nonce=" + authParams.get("nonce"));

        // 3. 【核心修改】严谨处理 URL 参数 (包括多值情况)
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null) {
                for (String value : values) {
                    // 每个值都参与签名，形成 k=v1, k=v2 的形式
                    paramPairs.add(key + "=" + (value == null ? "" : value));
                }
            }
        }
        if (StringUtils.hasText(requestBody)) {
            paramPairs.add("bodyHash=" + DigestUtils.sha256Hex(requestBody));
        }
        // 5. 【关键】按照整体字符串进行 ASCII 排序
        // 这样即使 key 相同，也会根据 value 的不同进行稳定排序
        Collections.sort(paramPairs);
        // 6. 拼接成最终待签名字符串
        return String.join("&", paramPairs);
    }
}