package top.openadexchange.dao.impl;

import org.springframework.stereotype.Service;

import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.SysDictDataDao;
import top.openadexchange.mapper.SysDictMapper;
import top.openadexchange.model.SysDict;

/**
 * 字典数据表 服务层实现。
 *
 * @author top.openadexchange
 * @since 2025-12-15
 */
@Service
public class SysDictDataDaoImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictDataDao {

}
