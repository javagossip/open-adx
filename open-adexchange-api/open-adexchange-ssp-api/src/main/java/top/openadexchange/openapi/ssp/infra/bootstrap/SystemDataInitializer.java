package top.openadexchange.openapi.ssp.infra.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import top.openadexchange.openapi.ssp.application.service.ApplicationWarmupService;

//系统数据初始化，包括索引库建立、广告数据缓存初始化等
@Component
public class SystemDataInitializer implements CommandLineRunner {

    private final ApplicationWarmupService applicationWarmupService;

    public SystemDataInitializer(ApplicationWarmupService applicationWarmupService) {
        this.applicationWarmupService = applicationWarmupService;
    }

    @Override
    public void run(String... args) throws Exception {
        applicationWarmupService.warmup();
    }
}
