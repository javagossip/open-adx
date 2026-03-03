package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.LogSamplingConfig;
import top.openadexchange.mapper.LogSamplingConfigMapper;
import top.openadexchange.dao.LogSamplingConfigDao;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-02-11
 */
@Service
public class LogSamplingConfigDaoImpl extends ServiceImpl<LogSamplingConfigMapper, LogSamplingConfig>  implements LogSamplingConfigDao{

}
