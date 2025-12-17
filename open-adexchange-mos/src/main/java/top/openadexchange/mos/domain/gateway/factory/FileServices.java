package top.openadexchange.mos.domain.gateway.factory;

import org.springframework.stereotype.Component;

import com.chaincoretech.epc.ExtensionRegistry;
import com.ruoyi.system.service.ISysConfigService;

import jakarta.annotation.Resource;
import top.openadexchange.mos.domain.gateway.FileService;

@Component
public class FileServices {

    @Resource
    private ISysConfigService sysConfigService;

    public FileService getDefaultFileService() {
        String defaultFileService = sysConfigService.selectConfigByKey("sys.fileservice.name");
        return ExtensionRegistry.getExtensionByKey(FileService.class, defaultFileService);
    }

    public FileService getFileServiceByType(String type) {
        return ExtensionRegistry.getExtensionByKey(FileService.class, type);
    }

    public FileService getDbFileService() {
        return ExtensionRegistry.getExtensionByKey(FileService.class, "db");
    }

    public FileService getLocalFileService() {
        return ExtensionRegistry.getExtensionByKey(FileService.class, "local");
    }

    public FileService getCosFileService() {
        return ExtensionRegistry.getExtensionByKey(FileService.class, "cos");
    }
}
