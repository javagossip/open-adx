package top.openadexchange.openapi.ssp.utils;


import top.openadexchange.commons.EnvUtils;

public class InstanceIdUtils {

    public static String getInstanceId() {
        return System.getenv("HOSTNAME") != null
                ? System.getenv("HOSTNAME")
                : NetUtil.getLocalhostStr() + ":" + EnvUtils.getServerPort();
    }
}
