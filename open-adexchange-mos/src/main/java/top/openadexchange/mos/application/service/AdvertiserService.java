package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.dao.AdvertiserDao;
import top.openadexchange.dto.AdvertiserAggregateDto;
import top.openadexchange.dto.AdvertiserAuditDto;
import top.openadexchange.dto.AdvertiserDto;
import top.openadexchange.dto.AdvertiserIndustryLicenseDto;
import top.openadexchange.dto.query.AdvertiserQueryDto;
import top.openadexchange.model.Advertiser;
import top.openadexchange.mos.application.converter.AdvertiserConverter;

@Service
@Slf4j
public class AdvertiserService {

    @Resource
    private AdvertiserDao advertiserDao;
    @Resource
    private AdvertiserConverter advertiserConverter;
    @Resource
    private AdvertiserIndustryLicenseService advertiserIndustryLicenseService;

    public Long addAdvertiser(AdvertiserDto advertiserDto) {
        log.info("addAdvertiser: {}", advertiserDto);
        Advertiser advertiser = advertiserConverter.from(advertiserDto);
        advertiserDao.save(advertiser);
        return advertiser.getId();
    }

    public Boolean updateAdvertiser(AdvertiserDto advertiserDto) {
        log.info("updateAdvertiser: {}", advertiserDto);
        Advertiser advertiser = advertiserConverter.from(advertiserDto);
        return advertiserDao.updateById(advertiser);
    }

    public Boolean deleteAdvertiser(Long id) {
        log.info("deleteAdvertiser: {}", id);
        return advertiserDao.removeById(id);
    }

    public AdvertiserDto getAdvertiser(Long id) {
        return advertiserConverter.toAdvertiserDto(advertiserDao.getById(id));
    }

    public Boolean enableAdvertiser(Long id) {
        return advertiserDao.updateChain().set(Advertiser::getStatus, 1).eq(Advertiser::getId, id).update();
    }

    public Boolean disableAdvertiser(Long id) {
        return advertiserDao.updateChain().set(Advertiser::getStatus, 0).eq(Advertiser::getId, id).update();
    }

    public Page<Advertiser> pageListAdvertisers(AdvertiserQueryDto queryDto) {
        return advertiserDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(Advertiser::getAdvertiserName, queryDto.getAdvertiserName())
                        .eq(Advertiser::getStatus, queryDto.getStatus()));
    }

    public AdvertiserAggregateDto getAdvertiserAggregateDetail(Long id) {
        Advertiser advertiser = advertiserDao.getById(id);
        AdvertiserDto advertiserDto = advertiserConverter.toAdvertiserDto(advertiser);
        List<AdvertiserIndustryLicenseDto> advertiserIndustryLicenses =
                advertiserIndustryLicenseService.getLicensesByAdvertiserId(id);
        AdvertiserAuditDto advertiserAudit = new AdvertiserAuditDto(advertiser.getId(),
                advertiser.getAuditStatus(),
                advertiser.getAuditReason(),
                advertiser.getAuditTime());
        return new AdvertiserAggregateDto(advertiserDto, advertiserIndustryLicenses, advertiserAudit);
    }
}