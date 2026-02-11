package top.openadexchange.mos.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.commons.StreamUtils;
import top.openadexchange.constants.enums.DomainEventType;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dao.LogSamplingConfigDao;
import top.openadexchange.dao.PublisherDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dto.LogSamplingConfigDto;
import top.openadexchange.dto.LogSamplingConfigQueryDto;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.LogSamplingConfig;
import top.openadexchange.model.Publisher;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.mos.application.factory.DomainEventFactory;

@Service
public class LogSamplingConfigService {

    @Resource
    private LogSamplingConfigDao logSamplingConfigDao;
    @Resource
    private DspDao dspDao;
    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;
    @Resource
    private PublisherDao publisherDao;
    @Resource
    private DomainEventDao domainEventDao;

    @Transactional
    public Long add(LogSamplingConfigDto dto) {
        LogSamplingConfig config = toEntity(dto);
        config.setId(null);
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        logSamplingConfigDao.save(config);
        domainEventDao.save(DomainEventFactory.create(DomainEventType.LOG_SAMPLING_CONFIG_CREATED.name(),
                config.getId()));
        return config.getId();
    }

    @Transactional
    public Boolean update(LogSamplingConfigDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        LogSamplingConfig config = toEntity(dto);
        boolean updated = logSamplingConfigDao.updateById(config);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.LOG_SAMPLING_CONFIG_UPDATED.name(),
                    config.getId()));
        }
        return updated;
    }

    @Transactional
    public Boolean delete(Long id) {
        boolean deleted = logSamplingConfigDao.removeById(id);
        if (deleted) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.LOG_SAMPLING_CONFIG_DELETED.name(), id));
        }
        return deleted;
    }

    public LogSamplingConfigDto getById(Long id) {
        LogSamplingConfig config = logSamplingConfigDao.getById(id);
        if (config == null) {
            return null;
        }
        return toDto(config);
    }

    @Transactional(readOnly = true)
    public Page<LogSamplingConfigDto> page(LogSamplingConfigQueryDto queryDto) {
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(LogSamplingConfig::getLogType, queryDto.getLogType())
                .eq(LogSamplingConfig::getMediaId, queryDto.getMediaId())
                .eq(LogSamplingConfig::getDspId, queryDto.getDspId())
                .eq(LogSamplingConfig::getAdSlotId, queryDto.getAdSlotId())
                .eq(LogSamplingConfig::getStatus, queryDto.getStatus())
                .orderBy(LogSamplingConfig::getId, false);

        Page<LogSamplingConfig> logSamplingConfigPage =
                logSamplingConfigDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()), wrapper);
        if (logSamplingConfigPage == null || !logSamplingConfigPage.hasRecords()) {
            return Page.of(queryDto.getPageNo(), queryDto.getPageSize());
        }
        List<LogSamplingConfig> records = logSamplingConfigPage.getRecords();

        List<Integer> dspIds = StreamUtils.toList(records, LogSamplingConfig::getDspId);
        List<Integer> publisherIds = StreamUtils.toList(records, LogSamplingConfig::getMediaId);
        List<Integer> adSlotIds = StreamUtils.toList(records, LogSamplingConfig::getAdSlotId);

        List<Dsp> dspList = CollectionUtils.isEmpty(dspIds) ? null : dspDao.listByIds(dspIds);
        List<SiteAdPlacement> adSlots =
                CollectionUtils.isEmpty(adSlotIds) ? null : siteAdPlacementDao.listByIds(adSlotIds);
        List<Publisher> publishers =
                CollectionUtils.isEmpty(publisherIds) ? null : publisherDao.listByIds(publisherIds);

        Map<Integer, Dsp> dspMap = StreamUtils.toMap(dspList, Dsp::getId, Function.identity());
        Map<Integer, SiteAdPlacement> adSlotMap =
                StreamUtils.toMap(adSlots, SiteAdPlacement::getId, Function.identity());
        Map<Integer, Publisher> publisherMap = StreamUtils.toMap(publishers, Publisher::getId, Function.identity());

        Page<LogSamplingConfigDto> resultPage =
                new Page<>(logSamplingConfigPage.getPageNumber(), logSamplingConfigPage.getPageSize());
        resultPage.setTotalPage(logSamplingConfigPage.getTotalPage());
        resultPage.setTotalRow(logSamplingConfigPage.getTotalRow());
        resultPage.setRecords(toDtoList(records, dspMap, adSlotMap, publisherMap));

        return resultPage;
    }

    private List<LogSamplingConfigDto> toDtoList(List<LogSamplingConfig> records,
            Map<Integer, Dsp> dspMap,
            Map<Integer, SiteAdPlacement> adSlotMap,
            Map<Integer, Publisher> publisherMap) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<LogSamplingConfigDto> resultList = new ArrayList<>();
        records.forEach(config -> {
            Dsp dsp = dspMap.get(config.getDspId());
            SiteAdPlacement adSlot = adSlotMap.get(config.getAdSlotId());
            Publisher publisher = publisherMap.get(config.getMediaId());

            LogSamplingConfigDto dto = new LogSamplingConfigDto();
            dto.setId(config.getId());
            dto.setLogType(config.getLogType());
            dto.setMediaId(config.getMediaId());
            dto.setMediaName(publisher == null ? null : publisher.getName());
            dto.setDspName(dsp == null ? null : dsp.getName());
            dto.setAdSlotName(adSlot == null ? null : adSlot.getName());
            dto.setDspId(config.getDspId());
            dto.setAdSlotId(config.getAdSlotId());
            dto.setSamplingRate(config.getSamplingRate());
            dto.setStatus(config.getStatus());
            dto.setCreateTime(config.getCreateTime());
            dto.setUpdateTime(config.getUpdateTime());
            resultList.add(dto);
        });
        return resultList;
    }

    private LogSamplingConfig toEntity(LogSamplingConfigDto dto) {
        if (dto == null) {
            return null;
        }
        LogSamplingConfig config = new LogSamplingConfig();
        config.setId(dto.getId());
        config.setLogType(dto.getLogType());
        config.setMediaId(dto.getMediaId());
        config.setDspId(dto.getDspId());
        config.setAdSlotId(dto.getAdSlotId());
        config.setSamplingRate(dto.getSamplingRate());
        config.setStatus(dto.getStatus());
        config.setCreateTime(dto.getCreateTime());
        config.setUpdateTime(dto.getUpdateTime());
        return config;
    }

    private LogSamplingConfigDto toDto(LogSamplingConfig config) {
        if (config == null) {
            return null;
        }
        Integer dspId = config.getDspId();
        Integer publisherId = config.getMediaId();
        Integer adSlotId = config.getAdSlotId();

        Dsp dsp = dspId == null ? null : dspDao.getById(dspId);
        Publisher publisher = publisherId == null ? null : publisherDao.getById(publisherId);
        SiteAdPlacement adSlot = adSlotId == null ? null : siteAdPlacementDao.getById(adSlotId);

        LogSamplingConfigDto dto = new LogSamplingConfigDto();
        dto.setDspName(dsp == null ? null : dsp.getName());
        dto.setAdSlotName(adSlot == null ? null : adSlot.getName());
        dto.setMediaName(publisher == null ? null : publisher.getName());
        dto.setId(config.getId());
        dto.setLogType(config.getLogType());
        dto.setMediaId(config.getMediaId());
        dto.setDspId(config.getDspId());
        dto.setAdSlotId(config.getAdSlotId());
        dto.setSamplingRate(config.getSamplingRate());
        dto.setStatus(config.getStatus());
        dto.setCreateTime(config.getCreateTime());
        dto.setUpdateTime(config.getUpdateTime());
        return dto;
    }
}

