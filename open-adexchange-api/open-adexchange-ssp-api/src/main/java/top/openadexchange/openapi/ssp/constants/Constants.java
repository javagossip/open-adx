package top.openadexchange.openapi.ssp.constants;

public class Constants {

    public interface CacheNames {

        String DSP = "dsp";
        String AD = "ad";
        String AD_GROUP = "ad_group";
    }

    public interface RegistryKeys {

        String SERVICE_NODE = "registry:service:nodes:%s";

        static String serviceNodeKey(String serviceName) {
            return String.format(SERVICE_NODE, serviceName);
        }
    }
}
