package top.openadexchange.openapi.dsp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.dsp.application.service.DspService;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;
import top.openadexchange.openapi.dsp.commons.OpenApiResponse;

import jakarta.annotation.Resource;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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
            if (authHeader == null || !authHeader.startsWith("clientId=")) {
                sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "认证信息缺失或格式错误");
                return false;
            }

            // 解析认证参数
            Map<String, String> authParams = parseAuthHeader(authHeader);
            String clientId = authParams.get("clientId");
            String timestamp = authParams.get("timestamp");
            String nonce = authParams.get("nonce");
            String signature = authParams.get("signature");

            // 验证必需参数
            if (clientId == null || timestamp == null || nonce == null || signature == null) {
                sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "认证参数缺失");
                return false;
            }

            // 验证时间戳（15分钟内有效）
            long timestampLong = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - timestampLong) > 15 * 60 * 1000) { // 15分钟
                sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "请求时间戳过期");
                return false;
            }

            // 查询DSP信息
            Dsp dsp = dspService.getDspByToken(clientId);
            if (dsp == null) {
                sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "无效的clientId");
                return false;
            }

            // 验证签名
            String computedSignature = generateSignature(request, dsp.getToken(), authParams);
            if (!computedSignature.equalsIgnoreCase(signature)) {
                sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "签名验证失败");
                return false;
            }

            // 检查nonce是否重复使用（防重放攻击）
            if (nonceCacheService.isNonceExists(nonce)) {
                sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "请求已使用，请勿重复请求");
                return false;
            }

            // 记录已使用的nonce
            nonceCacheService.recordNonce(nonce);

            return true;
        } catch (NumberFormatException e) {
            log.error("时间戳格式错误", e);
            sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "时间戳格式错误");
            return false;
        } catch (Exception e) {
            log.error("认证过程发生错误", e);
            sendErrorResponse(response, ApiErrorCode.AUTH_FAILED, "认证失败: " + e.getMessage());
            return false;
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
    private String generateSignature(HttpServletRequest request, String secretKey, Map<String, String> authParams) throws Exception {
        String stringToSign = SignatureUtils.buildStringToSign(request.getParameterMap(), authParams);
        return SignatureUtils.generateSignature(stringToSign, secretKey);
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, ApiErrorCode errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        OpenApiResponse<Object> errorResponse = OpenApiResponse.error(errorCode.getCode(), message);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        
        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonResponse);
            writer.flush();
        }
    }
}