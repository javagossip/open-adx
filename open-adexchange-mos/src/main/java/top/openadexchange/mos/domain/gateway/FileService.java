package top.openadexchange.mos.domain.gateway;

import org.springframework.web.multipart.MultipartFile;

import com.chaincoretech.epc.annotation.ExtensionPoint;

import top.openadexchange.model.SysFileData;
import top.openadexchange.model.SysFileMetadata;

@ExtensionPoint
public interface FileService {

    String uploadFile(MultipartFile file);

    SysFileData getFileData(String fileId);

    SysFileMetadata getFileMetadata(String fileId);
}
