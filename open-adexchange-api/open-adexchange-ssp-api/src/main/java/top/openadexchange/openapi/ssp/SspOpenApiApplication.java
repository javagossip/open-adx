package top.openadexchange.openapi.ssp;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.Objects;

@SpringBootApplication(scanBasePackages = "top.openadexchange")
@MapperScan(basePackages = {"top.openadexchange.mapper"})
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
}