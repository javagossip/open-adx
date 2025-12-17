package top.openadexchange.mos.infra.file;

import org.springframework.web.multipart.MultipartFile;

import com.chaincoretech.epc.annotation.Extension;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.SysFileDataDao;
import top.openadexchange.dao.SysFileMetadataDao;
import top.openadexchange.model.SysFileData;
import top.openadexchange.model.SysFileMetadata;
import top.openadexchange.mos.application.factory.SysFileFactory;
import top.openadexchange.mos.domain.gateway.FileService;

@Extension(keys = {"db"})
public class DbFileService implements FileService {

    @Resource
    private SysFileMetadataDao sysFileMetadataDao;
    @Resource
    private SysFileDataDao sysFileDataDao;

    @Override
    public String uploadFile(MultipartFile file) {
        SysFileMetadata metadata = SysFileFactory.fromMultipartFile(file);
        sysFileMetadataDao.save(metadata);
        SysFileData data = SysFileFactory.fromMultipartFile(file, metadata.getId());
        sysFileDataDao.save(data);

        return "/v1/files/" + metadata.getFileKey();
    }

    @Override
    public SysFileData getFileData(String fileId) {
        SysFileMetadata metadata = sysFileMetadataDao.getOne(QueryWrapper.create().eq("file_key", fileId));
        SysFileData data = sysFileDataDao.getOne(QueryWrapper.create().eq(SysFileData::getFileId, metadata.getId()));
        return data;
    }

    @Override
    public SysFileMetadata getFileMetadata(String fileId) {
        SysFileMetadata metadata = sysFileMetadataDao.getOne(QueryWrapper.create().eq("file_key", fileId));
        return metadata;
    }
}
