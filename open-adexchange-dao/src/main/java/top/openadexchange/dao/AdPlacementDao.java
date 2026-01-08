package top.openadexchange.dao;

import com.mybatisflex.core.service.IService;

import top.openadexchange.model.AdPlacement;

/**
 * 服务层。
 *
 * @author top.openadexchange
 * @since 2025-12-14
 */
public interface AdPlacementDao extends IService<AdPlacement> {

    Boolean enableAdPlacement(Integer id);

    Boolean disableAdPlacement(Integer id);

    AdPlacement getByTemplateCode(String nativeTemplateCode);
}