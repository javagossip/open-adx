package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.Industry;
import top.openadexchange.mapper.IndustryMapper;
import top.openadexchange.dao.IndustryDao;
import org.springframework.stereotype.Service;

/**
 * 广告行业字典表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-29
 */
@Service
public class IndustryDaoImpl extends ServiceImpl<IndustryMapper, Industry>  implements IndustryDao{

}
