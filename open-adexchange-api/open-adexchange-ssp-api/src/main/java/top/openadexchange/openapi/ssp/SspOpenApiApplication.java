package top.openadexchange.openapi.ssp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.openadexchange")
@MapperScan(basePackages = {"top.openadexchange.mapper"})
public class SspOpenApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SspOpenApiApplication.class, args);
    }
}