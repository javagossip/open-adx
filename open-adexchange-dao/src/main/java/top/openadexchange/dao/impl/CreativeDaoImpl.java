package top.openadexchange.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import top.openadexchange.dao.CreativeDao;
import top.openadexchange.mapper.CreativeMapper;
import top.openadexchange.model.Creative;

/**
 * 广告创意表，包括adx 自有创意以及 dsp 平台创意 服务层实现。
 *
 * @author top.openadexchange
 * @since 2026-01-03
 */
@Service
public class CreativeDaoImpl extends ServiceImpl<CreativeMapper, Creative> implements CreativeDao {

    @Override
    public List<Creative> getCreativesByDspCreativeIds(List<String> creativeIds) {
        Assert.isTrue(!CollectionUtils.isEmpty(creativeIds), "creativeIds is empty");
        return list(QueryWrapper.create().in(Creative::getDspCreativeId, creativeIds));
    }
}
