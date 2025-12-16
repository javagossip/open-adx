package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;
import top.openadexchange.model.District;

import java.util.List;

/**
 * 数据来源：https://github.com/eduosi/district.git 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
public interface DistrictDao extends IService<District> {

    List<District> getDistrictsByParentId(Integer parentId);
}
