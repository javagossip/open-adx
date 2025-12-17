package top.openadexchange.mos.infra.file;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.chaincoretech.epc.annotation.Extension;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.model.SysFileData;
import top.openadexchange.model.SysFileMetadata;
import top.openadexchange.mos.domain.gateway.FileService;
import top.openadexchange.tencentcloud.cos.CosConfigurationProperties;

//腾讯云COS云存储
@Extension(keys = {"cos"})
@Slf4j
public class CosFileService implements FileService {

    @Resource
    private CosConfigurationProperties cosConfigurationProperties;
    @Resource
    private COSClient cosClient;

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("uploadFile to tencent cloud COS: {}", file.getOriginalFilename());
        String fileKey = UUID.randomUUID().toString().replace("-", "");
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfigurationProperties.getBucket(),
                    fileKey,
                    file.getInputStream(),
                    metadata);
            cosClient.putObject(putObjectRequest);
            String fileUrl = String.format("%s://%s/%s",
                    cosConfigurationProperties.isHttps() ? "https" : "http",
                    cosConfigurationProperties.getDomain(),
                    fileKey);
            log.info("uploadFile to tencent cloud COS success: {}", fileUrl);
            return fileUrl;
        } catch (Exception ex) {
            log.error("uploadFile error: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
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
