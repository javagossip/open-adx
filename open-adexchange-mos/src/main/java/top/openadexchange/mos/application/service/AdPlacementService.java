package top.openadexchange.mos.application.service;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.AdPlacementDao;
import top.openadexchange.dto.AdPlacementDto;
import top.openadexchange.dto.query.AdPlacementQueryDto;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.mos.application.converter.AdPlacementConverter;

@Service
public class AdPlacementService {

    @Resource
    private AdPlacementDao adPlacementDao;
    @Resource
    private AdPlacementConverter adPlacementConverter;

    public Long addAdPlacement(AdPlacementDto adPlacementDto) {
        AdPlacement adPlacement = adPlacementConverter.from(adPlacementDto);
        adPlacementDao.save(adPlacement);
        return adPlacement.getId();
    }

    public Boolean updateAdPlacement(AdPlacementDto adPlacementDto) {
        AdPlacement adPlacement = adPlacementConverter.from(adPlacementDto);
        return adPlacementDao.updateById(adPlacement);
    }

    public Boolean deleteAdPlacement(Long id) {
        return adPlacementDao.removeById(id);
    }

    public AdPlacementDto getAdPlacement(Long id) {
        return adPlacementConverter.toAdPlacementDto(adPlacementDao.getById(id));
    }

    public Boolean enableAdPlacement(Long id) {
        return adPlacementDao.enableAdPlacement(id);
    }

    public Boolean disableAdPlacement(Long id) {
        return adPlacementDao.disableAdPlacement(id);
    }

    public Page<AdPlacement> pageListAdPlacements(AdPlacementQueryDto queryDto) {
        return adPlacementDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .eq(AdPlacement::getName, queryDto.getName())
                        .eq(AdPlacement::getAdFormat, queryDto.getAdFormat())
                        .eq(AdPlacement::getStatus, queryDto.getStatus()));
    }
}