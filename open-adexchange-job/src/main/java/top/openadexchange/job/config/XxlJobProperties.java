package top.openadexchange.job.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "xxl.job")
@Data
public class XxlJobProperties {

    public AdminConfig admin;
    private ExecutorConfig executor;

    @Data
    public static class AdminConfig {

        private String addresses;
        private String accessToken;
        private int timeout;
    }

    @Data
    public static class ExecutorConfig {

        private String appname;
        private String ip;
        private int port;
        private String logPath;
        private int logRetentionDays;
    }
}
