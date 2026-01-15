package top.openadexchange.dao;

import java.util.List;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.District;

/**
 * 数据来源：https://github.com/eduosi/district.git 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
public interface DistrictDao extends IService<District> {

    List<District> getDistrictsByParentId(Integer parentId);

    /**
     * 根据地区编码列表查询地区信息
     *
     * @param codes 地区编码列表
     * @return 地区信息列表
     */
    List<District> getDistrictsByCodes(List<String> codes);
}
