package top.openadexchange.mos.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.enums.DomainEventType;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.dao.PublisherDao;
import top.openadexchange.dao.SiteDao;
import top.openadexchange.dto.PublisherDto;
import top.openadexchange.dto.query.PublisherQueryDto;
import top.openadexchange.model.Publisher;
import top.openadexchange.model.Site;
import top.openadexchange.mos.application.converter.PublisherConverter;
import top.openadexchange.mos.application.factory.DomainEventFactory;
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
    @Resource
    private DomainEventDao domainEventDao;
    @Resource
    private SiteDao siteDao;

    @Transactional
    public Long addPublisher(PublisherDto publisherDto) {
        log.info("addPublisher: {}", publisherDto);
        SysUser sysUser = userFactory.forPublisher(publisherDto);
        sysUserService.insertUser(sysUser);

        Publisher publisher = publisherConverter.from(publisherDto);
        publisher.setUserId(sysUser.getUserId());
        publisherDao.save(publisher);

        domainEventDao.save(DomainEventFactory.create(DomainEventType.PUBLISHER_CREATED.name(), publisher.getId()));
        return publisher.getId();
    }

    @Transactional
    public Boolean updatePublisher(PublisherDto publisherDto) {
        log.info("updatePublisher: {}", publisherDto);
        Publisher publisher = publisherConverter.from(publisherDto);
        boolean updated = publisherDao.updateById(publisher);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.PUBLISHER_UPDATED.name(), publisher.getId()));
        }
        return updated;
    }

    @Transactional
    public Boolean deletePublisher(Long id) {
        log.info("deletePublisher: {}", id);
        boolean existsSites = siteDao.exists(QueryWrapper.create().eq(Site::getPublisherId, id));
        Assert.isTrue(!existsSites, "Exists sites, please delete sites first");
        Publisher publisher = publisherDao.getById(id);
        if (publisher == null) {
            log.warn("publisher not found: {}", id);
            return true;
        }
        if (publisher.getUserId() != null) {
            log.info("Delete publisher user: {}", publisher.getUserId());
            sysUserService.deleteUserById(publisher.getUserId());
        }
        publisherDao.removeById(id);
        domainEventDao.save(DomainEventFactory.create(DomainEventType.PUBLISHER_DELETED.name(), id));
        return true;
    }

    public PublisherDto getPublisher(Long id) {
        return publisherConverter.toPublisherDto(publisherDao.getById(id));
    }

    @Transactional
    public Boolean enablePublisher(Long id) {
        boolean updated = publisherDao.enablePublisher(id);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.PUBLISHER_UPDATED.name(), id));
        }
        return updated;
    }

    @Transactional
    public Boolean disablePublisher(Long id) {
        boolean updated = publisherDao.disablePublisher(id);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.PUBLISHER_UPDATED.name(), id));
        }
        return updated;
    }

    public Page<Publisher> pageListPublishers(PublisherQueryDto queryDto) {
        return publisherDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(Publisher::getName, queryDto.getName())
                        .eq(Publisher::getStatus, queryDto.getStatus()));
    }
}
