package top.openadexchange.commons.infra;

import org.hashids.Hashids;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import top.openadexchange.commons.service.EntityCodeGenerator;
import top.openadexchange.dao.SysSeqDao;
import top.openadexchange.model.SysSeq;

@Extension(key = "default")
@ConditionalOnClass(SysSeqDao.class)
public class DefaultEntityCodeGenerator implements EntityCodeGenerator {

    private static final Hashids hashids = new Hashids("open-adexchange", 8);
    @Resource
    private SysSeqDao sysSeqDao;

    @Override
    public String generateCode() {
        SysSeq sysSeq = SysSeq.builder().build();
        sysSeqDao.save(sysSeq);
        return hashids.encode(sysSeq.getId());
    }
}
