package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.RegionPkg;
import top.openadexchange.mapper.RegionPkgMapper;
import top.openadexchange.dao.RegionPkgDao;
import org.springframework.stereotype.Service;

/**
 * 地域包管理 服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-14
 */
@Service
public class RegionPkgDaoImpl extends ServiceImpl<RegionPkgMapper, RegionPkg>  implements RegionPkgDao{

}
