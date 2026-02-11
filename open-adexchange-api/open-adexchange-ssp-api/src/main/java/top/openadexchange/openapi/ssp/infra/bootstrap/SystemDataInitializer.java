package top.openadexchange.openapi.ssp.infra.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import top.openadexchange.openapi.ssp.application.service.WarmupService;
import top.openadexchange.openapi.ssp.application.service.RegistryService;

//系统数据初始化，包括索引库建立、广告数据缓存初始化等
@Component
public class SystemDataInitializer implements CommandLineRunner {

    private final WarmupService warmupService;
    private final RegistryService registryService;

    public SystemDataInitializer(WarmupService warmupService, RegistryService registryService) {
        this.warmupService = warmupService;
        this.registryService = registryService;
    }

    @Override
    public void run(String... args) throws Exception {
        registryService.register();
        warmupService.warmup();
    }
}
