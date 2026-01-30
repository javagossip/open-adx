package top.openadexchange.openapi.ssp.application.factory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.alibaba.fastjson2.JSON;

import top.openadexchange.commons.EnvUtils;
import top.openadexchange.commons.hash.HmacUtils;
import top.openadexchange.dto.TrackToken;

/**
 * 跟踪Token和URL生成工具类
 */
public class TrackTokenBuilder {

    private static final String TK_SECRET_KEY = "oax.tracking.secret";

    /**
     * 生成跟踪Token 格式：payloadBase64.signature
     *
     * @param trackToken 跟踪上下文信息
     * @return 跟踪Token
     */
    public static String buildTrackToken(TrackToken trackToken) {
        String secretKey = EnvUtils.getProperty(TK_SECRET_KEY);
        if (secretKey == null) {
            throw new IllegalStateException("Tracking secret key is not configured");
        }

        // 1. 将TrackToken序列化为JSON字节数组
        byte[] tokenBytes = JSON.toJSONString(trackToken).getBytes(StandardCharsets.UTF_8);

        // 2. 对payload进行Base64 URL编码（无padding）
        String payloadBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        // 3. 使用HMAC-SHA256生成签名
        String signature = HmacUtils.hmacSha256(secretKey, tokenBytes);

        // 4. 拼接payload和签名，格式：payloadBase64.signature
        return payloadBase64 + "." + signature;
    }

    /**
     * 生成曝光跟踪URL
     *
     * @param baseUrl 基础URL（如：http://your-server.com）
     * @param trackToken 跟踪上下文信息
     * @return 曝光跟踪URL
     */
    public static String buildImpTrackUrl(String baseUrl, TrackToken trackToken) {
        String tk = buildTrackToken(trackToken);
        return String.format("%s/v1/track/imp?tk=%s", baseUrl, tk);
    }

    /**
     * 生成点击跟踪URL
     *
     * @param baseUrl 基础URL（如：http://your-server.com）
     * @param trackToken 跟踪上下文信息
     * @return 点击跟踪URL
     */
    public static String buildClkTrackUrl(String baseUrl, TrackToken trackToken) {
        String tk = buildTrackToken(trackToken);
        return String.format("%s/v1/track/click?tk=%s", baseUrl, tk);
    }
}
