package top.openadexchange.tracking.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String COMMA = ",";

    private RequestUtils() {
    }

    private static boolean isInvalidIp(String ip) {
        return !StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip);
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        // 常见的代理请求头
        String ip = request.getHeader("x-forwarded-for");
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
            // 如果是本地回环访问，根据网卡取本机配置的IP
            if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
                try {
                    InetAddress confIp = InetAddress.getLocalHost();
                    ip = confIp.getHostAddress();
                } catch (UnknownHostException e) {
                    // 异常处理，保持默认值
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割if (ip != null && ip.length() > 15) {
        if (ip.contains(COMMA)) {
            ip = ip.split(COMMA)[0];
        }
        return ip;
    }
}
