package top.openadexchange.mos.api;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import top.openadexchange.dto.commons.ApiResponse;
import top.openadexchange.model.SysFileData;
import top.openadexchange.model.SysFileMetadata;
import top.openadexchange.mos.domain.gateway.FileService;
import top.openadexchange.mos.domain.gateway.factory.FileServices;

@RestController
@RequestMapping("/v1/files")
public class FileController {

    @Resource
    private FileServices fileServices;

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件",
            description = "上传文件, 返回文件上传成功之后的url")
    public ApiResponse<String> uploadFile(@RequestPart("file") MultipartFile file) {
        FileService fileService = fileServices.getDefaultFileService();
        return ApiResponse.success(fileService.uploadFile(file));
    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "下载文件")
    public void downloadFile(@PathVariable("fileId") String fileId, HttpServletResponse response) {
        SysFileData fileData = fileServices.getDefaultFileService().getFileData(fileId);
        SysFileMetadata fileMetadata = fileServices.getDefaultFileService().getFileMetadata(fileId);
        try {
            response.setContentType(fileMetadata.getContentType());
            StreamUtils.copy(fileData.getFileData(), response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
