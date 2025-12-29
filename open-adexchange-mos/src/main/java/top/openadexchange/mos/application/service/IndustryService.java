package top.openadexchange.mos.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import jakarta.annotation.Resource;
import top.openadexchange.dao.IndustryDao;
import top.openadexchange.dto.IndustryDto;
import top.openadexchange.dto.query.IndustryQueryDto;
import top.openadexchange.model.Industry;
import top.openadexchange.mos.application.converter.IndustryConverter;

@Service
public class IndustryService {

    @Resource
    private IndustryDao industryDao;

    @Resource
    private IndustryConverter industryConverter;

    public Long addIndustry(IndustryDto industryDto) {
        Industry industry = industryConverter.from(industryDto);
        industryDao.save(industry);
        return industry.getId().longValue();
    }

    public Boolean updateIndustry(IndustryDto industryDto) {
        Industry industry = industryConverter.from(industryDto);
        return industryDao.updateById(industry);
    }

    public Boolean deleteIndustry(Long id) {
        return industryDao.removeById(id);
    }

    public IndustryDto getIndustry(Long id) {
        Industry industry = industryDao.getById(id);
        return industryConverter.toIndustryDto(industry);
    }

    public Page<Industry> pageListIndustry(IndustryQueryDto queryDto) {
        return industryDao.page(Page.of(queryDto.getPageNo(), queryDto.getPageSize()),
                QueryWrapper.create()
                        .like(Industry::getCode, queryDto.getCode())
                        .like(Industry::getName, queryDto.getName())
                        .eq(Industry::getRiskLevel, queryDto.getRiskLevel())
                        .eq(Industry::getNeedLicense, queryDto.getNeedLicense())
                        .eq(Industry::getStatus, queryDto.getStatus()));
    }

    public List<Industry> listIndustriesByParentId(Long parentId) {
        if (parentId == null) {
            return industryDao.list(QueryWrapper.create().isNull(Industry::getParentId));
        }
        return industryDao.list(QueryWrapper.create().eq(Industry::getParentId, parentId));
    }
}