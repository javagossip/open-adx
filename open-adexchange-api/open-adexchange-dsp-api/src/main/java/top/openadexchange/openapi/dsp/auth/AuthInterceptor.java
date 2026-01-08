package top.openadexchange.openapi.dsp.auth;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.dsp.application.service.DspService;
import top.openadexchange.openapi.dsp.auth.exception.AuthException;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private DspService dspService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private NonceCacheService nonceCacheService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        try {
            // 从请求头中获取认证信息
            String authHeader = request.getHeader("Authorization");
            if (!StringUtils.hasText(authHeader)) {
                throw new AuthException("认证信息缺失, Header: Authorization");
            }
            // 解析认证参数
            Map<String, String> authParams = parseAuthHeader(authHeader);
            String clientId = authParams.get("clientId");
            String timestamp = authParams.get("timestamp");
            String nonce = authParams.get("nonce");
            String signature = authParams.get("signature");

            // 验证必需参数
            if (clientId == null || timestamp == null || nonce == null || signature == null) {
                log.error("认证参数缺失");
                throw new AuthException("认证参数缺失");
            }
            // 验证时间戳（15分钟内有效）
            long timestampLong = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - timestampLong) > 15 * 60 * 1000) { // 15分钟
                throw new AuthException("请求时间戳已过期");
            }
            // 查询DSP信息
            Dsp dsp = dspService.getDspByClientId(clientId);
            if (dsp == null) {
                throw new AuthException("DSP不存在或已停用");
            }
            // 验证签名
            String computedSignature = generateSignature(request, dsp.getToken(), authParams);
            if (!computedSignature.equalsIgnoreCase(signature)) {
                throw new AuthException("签名错误");
            }

            // 检查nonce是否重复使用（防重放攻击）
            if (nonceCacheService.isNonceExists(nonce)) {
                throw new AuthException("请求已使用，请勿重复请求");
            }
            // 记录已使用的nonce
            nonceCacheService.recordNonce(nonce);
            return true;
        } catch (Exception ex) {
            if (ex instanceof AuthException) {
                throw ex;
            }
            log.error("认证过程发生错误", ex);
            throw new AuthException("认证过程发生错误: " + ex.getMessage());
        }
    }

    /**
     * 解析认证头信息
     */
    private Map<String, String> parseAuthHeader(String authHeader) {
        Map<String, String> params = new HashMap<>();
        String[] parts = authHeader.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.contains("=")) {
                int eqIndex = part.indexOf('=');
                String key = part.substring(0, eqIndex);
                String value = part.substring(eqIndex + 1);
                params.put(key, value);
            }
        }
        return params;
    }

    /**
     * 生成签名
     */
    private String generateSignature(HttpServletRequest request,
            String secretKey,
            Map<String, String> authParams) throws Exception {
        String stringToSign = SignatureUtils.buildStringToSign(request.getParameterMap(),
                authParams,
                StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8));
        Assert.hasText(stringToSign, "待签名数据为空");
        return SignatureUtils.generateSignature(stringToSign, secretKey);
    }
}