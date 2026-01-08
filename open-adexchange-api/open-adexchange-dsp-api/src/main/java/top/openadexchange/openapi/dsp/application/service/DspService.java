package top.openadexchange.openapi.dsp.application.service;

import com.mybatisflex.core.query.QueryWrapper;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import top.openadexchange.dao.DspDao;
import top.openadexchange.model.Dsp;

@Service
public class DspService {

    @Resource
    private DspDao dspDao;

    public Dsp getDspByToken(String token) {
        return dspDao.getDspByToken(token);
    }

    public Dsp getDspByClientId(String clientId) {
        return dspDao.getOne(QueryWrapper.create().eq(Dsp::getDspId, clientId).eq(Dsp::getStatus, 1));
    }
}
