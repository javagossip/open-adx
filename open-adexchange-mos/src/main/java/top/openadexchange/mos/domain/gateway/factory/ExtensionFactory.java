package top.openadexchange.mos.domain.gateway.factory;

import com.ruoyi.system.service.ISysConfigService;

import jakarta.annotation.Resource;

public abstract class ExtensionFactory {

    @Resource
    protected ISysConfigService sysConfigService;
}
