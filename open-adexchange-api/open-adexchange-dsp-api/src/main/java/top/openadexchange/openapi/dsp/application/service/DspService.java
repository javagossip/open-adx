package top.openadexchange.openapi.dsp.application.service;

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
}
