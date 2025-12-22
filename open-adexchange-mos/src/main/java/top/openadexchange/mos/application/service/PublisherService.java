package top.openadexchange.mos.application.service;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.dao.PublisherDao;
import top.openadexchange.dto.PublisherDto;
import top.openadexchange.dto.query.PublisherQueryDto;
import top.openadexchange.model.Publisher;
import top.openadexchange.mos.application.converter.PublisherConverter;
import top.openadexchange.mos.application.factory.UserFactory;

@Service
@Slf4j
public class PublisherService {

    @Resource
    private PublisherDao publisherDao;
    @Resource
    private PublisherConverter publisherConverter;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private UserFactory userFactory;

    public Long addPublisher(PublisherDto publisherDto) {
        log.info("addPublisher: {}", publisherDto);
        SysUser sysUser = userFactory.forPublisher(publisherDto);
        sysUserService.insertUser(sysUser);

        Publisher publisher = publisherConverter.from(publisherDto);
        publisher.setUserId(sysUser.getUserId());
        publisherDao.save(publisher);

        return publisher.getId();
    }

    public Boolean updatePublisher(PublisherDto publisherDto) {
        log.info("updatePublisher: {}", publisherDto);
        Publisher publisher = publisherConverter.from(publisherDto);
        return publisherDao.updateById(publisher);
    }

    public Boolean deletePublisher(Long id) {
        log.info("deletePublisher: {}", id);
        publisherDao.removeById(id);
        Publisher publisher = publisherDao.getById(id);
        sysUserService.deleteUserById(publisher.getUserId());
        return true;
    }

    public PublisherDto getPublisher(Long id) {
        return publisherConverter.toPublisherDto(publisherDao.getById(id));
    }

    public Boolean enablePublisher(Long id) {
        return publisherDao.enablePublisher(id);
    }

    public Boolean disablePublisher(Long id) {
        return publisherDao.disablePublisher(id);
    }

    public Page<Publisher> pageListPublishers(PublisherQueryDto queryDto) {
        return publisherDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(Publisher::getName, queryDto.getName())
                        .eq(Publisher::getStatus, queryDto.getStatus()));
    }
}
