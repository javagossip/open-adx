package top.openadexchange.mos.infra.file;

import com.chaincoretech.epc.annotation.Extension;

import org.springframework.web.multipart.MultipartFile;

import top.openadexchange.model.SysFileData;
import top.openadexchange.model.SysFileMetadata;
import top.openadexchange.mos.domain.gateway.FileService;

@Extension(keys={"local"})
public class LocalFileService implements FileService {

    @Override
    public String uploadFile(MultipartFile file) {
        return "";
    }

    @Override
    public SysFileData getFileData(String fileId) {
        return null;
    }

    @Override
    public SysFileMetadata getFileMetadata(String fileId) {
        return null;
    }
}
