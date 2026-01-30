package top.openadexchange.openapi.ssp.utils;

import java.net.*;
import java.util.Enumeration;

public class NetUtil {

    public static final String LOCAL_IP = getLocalhostStr();

    /**
     * 获取本地真实 IP 地址 优化点：过滤掉回环地址 (127.0.0.1)、虚拟网卡地址等
     */
    public static String getLocalhostStr() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 过滤掉回环网卡、虚拟网卡和未启动的网卡
                if (iface.isLoopback() || iface.isVirtual() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // 只取 IPv4 地址
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            // 异常兜底
            return "127.0.0.1";
        }
        return "127.0.0.1";
    }

    /**
     * 检查本地端口是否可用
     */
    public static boolean isPortAvailable(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port), 100);
            return false; // 能连上说明被占用了
        } catch (Exception ex) {
            return true; // 连不上说明可用
        }
    }
}