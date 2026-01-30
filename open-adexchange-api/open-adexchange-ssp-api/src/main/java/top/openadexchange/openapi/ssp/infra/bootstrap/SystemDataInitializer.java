package top.openadexchange.openapi.ssp.infra.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import top.openadexchange.openapi.ssp.application.service.ApplicationWarmupService;
import top.openadexchange.openapi.ssp.application.service.RegistryService;
import top.openadexchange.openapi.ssp.constants.Constants.RegistryKeys;

//系统数据初始化，包括索引库建立、广告数据缓存初始化等
@Component
public class SystemDataInitializer implements CommandLineRunner {

    private final ApplicationWarmupService applicationWarmupService;
    private final RegistryService registryService;

    public SystemDataInitializer(ApplicationWarmupService applicationWarmupService, RegistryService registryService) {
        this.applicationWarmupService = applicationWarmupService;
        this.registryService = registryService;
    }

    @Override
    public void run(String... args) throws Exception {
        registryService.register();
        applicationWarmupService.warmup();
    }
}
