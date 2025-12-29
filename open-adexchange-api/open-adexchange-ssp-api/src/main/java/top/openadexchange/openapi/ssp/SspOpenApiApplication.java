package top.openadexchange.openapi.ssp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "top.openadexchange")
public class SspOpenApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SspOpenApiApplication.class, args);
    }
}
