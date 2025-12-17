package top.openadexchange.mos.domain.gateway;

import org.springframework.web.multipart.MultipartFile;

import com.chaincoretech.epc.annotation.ExtensionPoint;

@ExtensionPoint
public interface FileService {

    String uploadFile(MultipartFile file);
}
