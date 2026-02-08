package top.openadexchange.tracking;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;

@SpringBootApplication(scanBasePackages = {"top.openadexchange"})
@EnableScheduling
public class OaxTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaxTrackingApplication.class, args);
    }

    @Bean(name = "jasyptStringEncryptor")
    @Primary
    public StringEncryptor stringEncryptor(Environment environment) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(Objects.requireNonNull(environment.getProperty("jasypt.encryptor.password")));
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        return encryptor;
    }
}
