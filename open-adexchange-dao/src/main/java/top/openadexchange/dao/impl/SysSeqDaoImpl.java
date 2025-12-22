package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.SysSeqDao;
import top.openadexchange.mapper.SysSeqMapper;
import top.openadexchange.model.SysSeq;

/**
 * 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-18
 */
@Service
public class SysSeqDaoImpl extends ServiceImpl<SysSeqMapper, SysSeq> implements SysSeqDao {

}
