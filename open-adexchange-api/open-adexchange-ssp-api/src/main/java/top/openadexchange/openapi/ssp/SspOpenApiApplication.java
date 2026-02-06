package top.openadexchange.openapi.ssp;

import java.util.Objects;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication(scanBasePackages = "top.openadexchange")
@MapperScan(basePackages = {"top.openadexchange.mapper"})
@EnableScheduling
public class SspOpenApiApplication {

    private static final String ENCRYPTOR_ALGORITHM = "PBEWithMD5AndTripleDES";
    private static final String ENCRYPTOR_ENV_KEY = "jasypt.encryptor.password";

    public static void main(String[] args) {
        SpringApplication.run(SspOpenApiApplication.class, args);
    }

    @Bean(name = "jasyptStringEncryptor")
    @Primary
    public StringEncryptor stringEncryptor(Environment environment) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(Objects.requireNonNull(environment.getProperty(ENCRYPTOR_ENV_KEY)));
        encryptor.setAlgorithm(ENCRYPTOR_ALGORITHM);
        return encryptor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("sync-metrics-task-");
        scheduler.initialize();
        return scheduler;
    }
}