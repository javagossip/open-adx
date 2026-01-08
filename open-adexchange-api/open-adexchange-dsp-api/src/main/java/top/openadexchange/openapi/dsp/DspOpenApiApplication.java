package top.openadexchange.openapi.dsp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"top.openadexchange"})
@MapperScan(basePackages = {"top.openadexchange.mapper"})
public class DspOpenApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DspOpenApiApplication.class, args);
    }
}
