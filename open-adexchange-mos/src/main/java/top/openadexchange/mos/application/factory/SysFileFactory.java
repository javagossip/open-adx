package top.openadexchange.mos.application.factory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import top.openadexchange.model.SysFileData;
import top.openadexchange.model.SysFileMetadata;

public class SysFileFactory {

    public static SysFileMetadata fromMultipartFile(MultipartFile file) {
        SysFileMetadata metadata = new SysFileMetadata();
        metadata.setOriginalName(file.getOriginalFilename());
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());

        // 生成唯一的文件键
        String fileKey = UUID.randomUUID().toString().replace("-", "");
        metadata.setFileKey(fileKey);
        // 计算文件哈希值
        metadata.setFileHash(calculateMD5(file));
        // 设置初始状态和时间
        metadata.setStatus(1); // 1表示正常状态
        metadata.setCreatedAt(LocalDateTime.now());
        return metadata;
    }

    public static SysFileData fromMultipartFile(MultipartFile file, Long fileId) {
        SysFileData data = new SysFileData();
        data.setFileId(fileId);
        try {
            data.setFileData(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("无法获取文件输入流", e);
        }
        return data;
    }

    /**
     * 计算文件的MD5哈希值
     *
     * @param file MultipartFile对象
     * @return MD5哈希值
     */

    // 使用 DigestUtils 计算文件 MD5
    private static String calculateMD5(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("无法计算文件哈希值", e);
        }
    }
}