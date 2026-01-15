package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.DistrictDao;
import top.openadexchange.mapper.DistrictMapper;
import top.openadexchange.model.District;

/**
 * 数据来源：https://github.com/eduosi/district.git 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Service
public class DistrictDaoImpl extends ServiceImpl<DistrictMapper, District> implements DistrictDao {

    @Override
    public List<District> getDistrictsByParentId(Integer parentId) {
        return list(QueryWrapper.create().eq(District::getParentId, parentId == null ? 0 : parentId));
    }

    @Override
    public List<District> getDistrictsByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return list(QueryWrapper.create().in(District::getCode, codes));
    }
}
