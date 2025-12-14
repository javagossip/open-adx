package top.openadexchange.dao.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import top.openadexchange.model.Dsp;
import top.openadexchange.mapper.DspMapper;
import top.openadexchange.dao.DspDao;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-13
 */
@Service
public class DspDaoImpl extends ServiceImpl<DspMapper, Dsp>  implements DspDao{

}
