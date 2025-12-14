package top.openadexchange.mos;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Objects;

@SpringBootApplication(scanBasePackages = {"top.openadexchange"})
@MapperScan(basePackages = {"top.openadexchange.mapper"})
@EnableAsync
public class AdExchangeMosApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdExchangeMosApplication.class, args);

//        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//        encryptor.setPassword("top.openadexchange");
//        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
//
//        System.out.println(encryptor.encrypt("host!@#$1234"));
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
