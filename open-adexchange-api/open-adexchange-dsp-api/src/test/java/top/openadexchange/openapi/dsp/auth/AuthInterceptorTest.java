package top.openadexchange.openapi.dsp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.dsp.application.service.DspService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @Mock
    private DspService dspService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AuthInterceptor authInterceptor;
    private ObjectMapper objectMapper;
    private NonceCacheService nonceCacheService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        nonceCacheService = new NonceCacheService();
        authInterceptor = new AuthInterceptor();
        
        // 使用反射设置依赖
        try {
            java.lang.reflect.Field dspServiceField = AuthInterceptor.class.getDeclaredField("dspService");
            dspServiceField.setAccessible(true);
            dspServiceField.set(authInterceptor, dspService);

            java.lang.reflect.Field objectMapperField = AuthInterceptor.class.getDeclaredField("objectMapper");
            objectMapperField.setAccessible(true);
            objectMapperField.set(authInterceptor, objectMapper);

            java.lang.reflect.Field nonceCacheServiceField = AuthInterceptor.class.getDeclaredField("nonceCacheService");
            nonceCacheServiceField.setAccessible(true);
            nonceCacheServiceField.set(authInterceptor, nonceCacheService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test dependencies", e);
        }
    }

    @Test
    void testPreHandleWithoutAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        boolean result = authInterceptor.preHandle(request, response, null);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testPreHandleWithInvalidAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("invalid-header");

        boolean result = authInterceptor.preHandle(request, response, null);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testPreHandleMissingAuthParams() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("clientId=123");

        boolean result = authInterceptor.preHandle(request, response, null);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testPreHandleExpiredTimestamp() throws Exception {
        String authHeader = "clientId=testClientId,timestamp=" + (System.currentTimeMillis() - 20 * 60 * 1000) + ",nonce=testNonce,signature=testSignature";
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        boolean result = authInterceptor.preHandle(request, response, null);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testPreHandleInvalidClientId() throws Exception {
        String authHeader = "clientId=invalidClientId,timestamp=" + System.currentTimeMillis() + ",nonce=testNonce,signature=testSignature";
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        when(dspService.getDspByToken("invalidClientId")).thenReturn(null);

        boolean result = authInterceptor.preHandle(request, response, null);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(dspService).getDspByToken("invalidClientId");
    }

    @Test
    void testPreHandleValidRequest() throws Exception {
        String clientId = "validClientId";
        String token = "validToken";
        String nonce = "testNonce";
        long timestamp = System.currentTimeMillis();
        
        String authHeader = "clientId=" + clientId + ",timestamp=" + timestamp + ",nonce=" + nonce + ",signature=testSignature";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getParameterMap()).thenReturn(new HashMap<>());

        Dsp dsp = new Dsp();
        dsp.setToken(token);
        when(dspService.getDspByToken(clientId)).thenReturn(dsp);

        // 生成正确的签名
        Map<String, String> authParams = new HashMap<>();
        authParams.put("clientId", clientId);
        authParams.put("timestamp", String.valueOf(timestamp));
        authParams.put("nonce", nonce);
        
        String stringToSign = SignatureUtils.buildStringToSign(new HashMap<>(), authParams);
        String correctSignature = SignatureUtils.generateSignature(stringToSign, token);
        
        // 更新请求头以包含正确的签名
        authHeader = "clientId=" + clientId + ",timestamp=" + timestamp + ",nonce=" + nonce + ",signature=" + correctSignature;
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        boolean result = authInterceptor.preHandle(request, response, null);

        assertTrue(result);
    }

    @Test
    void testPreHandleDuplicateNonce() throws Exception {
        String clientId = "validClientId";
        String token = "validToken";
        String nonce = "testNonce";
        long timestamp = System.currentTimeMillis();
        
        String authHeader = "clientId=" + clientId + ",timestamp=" + timestamp + ",nonce=" + nonce + ",signature=testSignature";
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getParameterMap()).thenReturn(new HashMap<>());

        Dsp dsp = new Dsp();
        dsp.setToken(token);
        when(dspService.getDspByToken(clientId)).thenReturn(dsp);

        // 首先记录一个nonce
        nonceCacheService.recordNonce(nonce);

        // 生成正确的签名
        Map<String, String> authParams = new HashMap<>();
        authParams.put("clientId", clientId);
        authParams.put("timestamp", String.valueOf(timestamp));
        authParams.put("nonce", nonce);
        
        String stringToSign = SignatureUtils.buildStringToSign(new HashMap<>(), authParams);
        String correctSignature = SignatureUtils.generateSignature(stringToSign, token);
        
        // 更新请求头以包含正确的签名
        authHeader = "clientId=" + clientId + ",timestamp=" + timestamp + ",nonce=" + nonce + ",signature=" + correctSignature;
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        boolean result = authInterceptor.preHandle(request, response, null);

        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}