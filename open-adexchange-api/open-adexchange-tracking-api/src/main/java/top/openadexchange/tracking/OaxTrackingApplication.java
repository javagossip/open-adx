package top.openadexchange.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"top.openadexchange"})
public class OaxTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaxTrackingApplication.class, args);
    }
}
