package top.openadexchange.commons;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvUtils implements EnvironmentAware {

    private static Environment environment;

    public static String getAppName() {
        return environment.getProperty("spring.application.name");
    }

    public static String getServerPort() {
        return environment.getProperty("server.port");
    }

    @Override
    public void setEnvironment(Environment env) {
        environment = env;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return environment.getProperty(key, targetType);
    }

    public static boolean getBooleanProperty(String key) {
        Boolean value = environment.getProperty(key, Boolean.class);
        return value == null ? false : value;
    }

    public static int getIntProperty(String key) {
        Integer value = environment.getProperty(key, Integer.class);
        return value == null ? 0 : value;
    }
}
