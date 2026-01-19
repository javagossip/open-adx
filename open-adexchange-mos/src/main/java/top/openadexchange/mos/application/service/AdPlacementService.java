package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import top.openadexchange.constants.enums.AdFormat;
import top.openadexchange.constants.enums.DomainEventType;
import top.openadexchange.dao.AdPlacementDao;
import top.openadexchange.dao.DomainEventDao;
import top.openadexchange.dao.NativeAssetDao;
import top.openadexchange.dto.AdPlacementDto;
import top.openadexchange.dto.query.AdPlacementQueryDto;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.NativeAsset;
import top.openadexchange.mos.application.converter.AdPlacementConverter;
import top.openadexchange.mos.application.converter.NativeAssetConverter;
import top.openadexchange.mos.application.factory.DomainEventFactory;

import static top.openadexchange.model.table.AdPlacementTableDef.*;

@Service
public class AdPlacementService {

    @Resource
    private AdPlacementDao adPlacementDao;
    @Resource
    private AdPlacementConverter adPlacementConverter;
    @Resource
    private NativeAssetConverter nativeAssetConverter;
    @Resource
    private NativeAssetDao nativeAssetDao;
    @Resource
    private DomainEventDao domainEventDao;

    @Transactional
    public Integer addAdPlacement(AdPlacementDto adPlacementDto) {
        AdPlacement adPlacement = adPlacementConverter.from(adPlacementDto);
        adPlacementDao.save(adPlacement);
        List<NativeAsset> nativeAssets = nativeAssetConverter.from(adPlacement.getId(), adPlacementDto.getNativeAd());
        if (nativeAssets != null && !nativeAssets.isEmpty()) {
            nativeAssetDao.saveBatch(nativeAssets);
        }
        domainEventDao.save(DomainEventFactory.create(DomainEventType.AD_PLACEMENT_CREATED.name(),
                adPlacement.getId()));
        return adPlacement.getId();
    }

    public Boolean updateAdPlacement(AdPlacementDto adPlacementDto) {
        AdPlacement adPlacement = adPlacementConverter.from(adPlacementDto);
        List<NativeAsset> nativeAssets = nativeAssetConverter.from(adPlacement.getId(), adPlacementDto.getNativeAd());
        if (nativeAssets != null && !nativeAssets.isEmpty()) {
            nativeAssetDao.updateNativeAssetsByAdPlacementId(adPlacement.getId(), nativeAssets);
        }
        boolean updated = adPlacementDao.updateById(adPlacement);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.AD_PLACEMENT_UPDATED.name(),
                    adPlacement.getId()));
        }
        return updated;
    }

    public Boolean deleteAdPlacement(Integer id) {
        boolean deleted = adPlacementDao.removeById(id);
        if (deleted) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.AD_PLACEMENT_DELETED.name(), id));
        }
        return deleted;
    }

    public AdPlacementDto getAdPlacement(Integer id) {
        AdPlacement adPlacement = adPlacementDao.getById(id);
        AdPlacementDto adPlacementDto = adPlacementConverter.toAdPlacementDto(adPlacementDao.getById(id));
        if (adPlacement != null && AdFormat.NATIVE == AdFormat.valueOf(adPlacement.getAdFormat())) {
            adPlacementDto.setNativeAd(nativeAssetConverter.toNativeAssetDtoList(nativeAssetDao.listByAdPlacementId(id)));
        }
        return adPlacementDto;
    }

    public Boolean enableAdPlacement(Integer id) {
        boolean updated = adPlacementDao.enableAdPlacement(id);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.AD_PLACEMENT_UPDATED.name(), id));
        }
        return updated;
    }

    public Boolean disableAdPlacement(Integer id) {
        boolean updated = adPlacementDao.disableAdPlacement(id);
        if (updated) {
            domainEventDao.save(DomainEventFactory.create(DomainEventType.AD_PLACEMENT_UPDATED.name(), id));
        }
        return updated;
    }

    public Page<AdPlacement> pageListAdPlacements(AdPlacementQueryDto queryDto) {
        return adPlacementDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(AdPlacement::getName, queryDto.getName())
                        .eq(AdPlacement::getCode, queryDto.getCode())
                        .eq(AdPlacement::getAdFormat, queryDto.getAdFormat())
                        .eq(AdPlacement::getStatus, queryDto.getStatus()));
    }

    public List<AdPlacement> searchAdPlacements(String searchKey, Integer size) {
        return adPlacementDao.list(QueryWrapper.create()
                .where(AD_PLACEMENT.NAME.like(searchKey))
                .limit(size == null ? 20 : size));
    }
}