package top.openadexchange.mos.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryMethods;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import top.openadexchange.commons.FloorPriceUtils;
import top.openadexchange.dao.FloorPriceDao;
import top.openadexchange.dao.SiteAdPlacementDao;
import top.openadexchange.dto.FloorPriceDto;
import top.openadexchange.dto.FloorPriceQueryDto;
import top.openadexchange.model.table.FloorPriceTableDef;
import top.openadexchange.mos.application.converter.FloorPriceConverter;
import top.openadexchange.model.FloorPrice;
import top.openadexchange.model.SiteAdPlacement;

import java.util.List;
import java.util.stream.Collectors;

import static top.openadexchange.model.table.FloorPriceTableDef.*;
import static top.openadexchange.model.table.IndustryTableDef.*;
import static top.openadexchange.model.table.RegionPkgTableDef.*;
import static top.openadexchange.model.table.SiteAdPlacementTableDef.*;

@Service
public class FloorPriceService {

    @Resource
    private FloorPriceDao floorPriceDao;

    @Resource
    private FloorPriceConverter floorPriceConverter;

    @Resource
    private SiteAdPlacementDao siteAdPlacementDao;

    public Integer addFloorPrice(FloorPriceDto floorPriceDto) {
        FloorPrice floorPrice = floorPriceConverter.from(floorPriceDto);
        floorPrice.setStatus(1); // 默认启用
        floorPriceDao.save(floorPrice);
        return floorPrice.getId();
    }

    public Boolean updateFloorPrice(FloorPriceDto floorPriceDto) {
        if (floorPriceDto.getSiteAdPlacementId() == null) {
            throw new IllegalArgumentException("媒体广告位id不能为空");
        }

        QueryWrapper wrapper = QueryWrapper.create().eq(FloorPrice::getId, floorPriceDto.getId());

        FloorPrice floorPrice = floorPriceDao.getOne(wrapper);
        if (floorPrice == null) {
            throw new RuntimeException("未找到对应的底价记录");
        }

        floorPrice.setFloorPrice(FloorPriceUtils.yuanToCent(floorPriceDto.getFloorPrice()));
        floorPrice.setIndustryId(floorPriceDto.getIndustryId());
        floorPrice.setRegionLevelId(floorPriceDto.getRegionLevelId());

        return floorPriceDao.updateById(floorPrice);
    }

    public Boolean deleteFloorPrice(Integer id) {
        return floorPriceDao.removeById(id);
    }

    public FloorPriceDto getFloorPriceById(Integer id) {
        FloorPrice floorPrice = floorPriceDao.getById(id);
        if (floorPrice == null) {
            return null;
        }
        return floorPriceConverter.toDto(floorPrice);
    }

    public Page<FloorPriceDto> getFloorPricePage(FloorPriceQueryDto queryDto) {
        return floorPriceDao.pageAs(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .select("t.id",
                                "t.site_ad_placement_id",
                                "t.floor_price",
                                "t.industry_id",
                                "t.region_level_id",
                                "t1.name AS site_ad_placement_name",
                                "t2.name AS industry_name",
                                "t3.name AS region_level_name")
                        .from(FLOOR_PRICE)
                        .as("t")
                        .leftJoin(SITE_AD_PLACEMENT)
                        .as("t1")
                        .on(FLOOR_PRICE.SITE_AD_PLACEMENT_ID.eq(SITE_AD_PLACEMENT.ID))
                        .leftJoin(INDUSTRY)
                        .as("t2")
                        .on(FLOOR_PRICE.INDUSTRY_ID.eq(INDUSTRY.ID))
                        .leftJoin(REGION_PKG)
                        .as("t3")
                        .on(FLOOR_PRICE.REGION_LEVEL_ID.eq(REGION_PKG.ID))
                        .eq(FloorPrice::getSiteAdPlacementId, queryDto.getSiteAdPlacementId())
                        .eq(FloorPrice::getStatus, queryDto.getStatus())
                        .eq(FloorPrice::getIndustryId, queryDto.getIndustryId())
                        .eq(FloorPrice::getRegionLevelId, queryDto.getRegionLevelId()),
                FloorPriceDto.class);
    }
}