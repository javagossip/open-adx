package top.openadexchange.mos.infra.file;

import org.springframework.web.multipart.MultipartFile;

import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.mos.domain.gateway.FileService;

@Extension(keys = {"local"})
public class LocalFileService implements FileService {

    @Override
    public String uploadFile(MultipartFile file) {
        return "";
    }
}
