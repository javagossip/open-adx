package top.openadexchange.mos.domain.gateway;

import org.springframework.stereotype.Component;

@Component
public class FileServiceFactory {

    public FileService getDefaultFileService() {
        return null;
    }

    public FileService getFileService(String type) {
        throw new UnsupportedOperationException("不支持的文件服务类型");
    }
}
