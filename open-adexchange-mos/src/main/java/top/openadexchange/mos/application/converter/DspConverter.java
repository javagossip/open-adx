package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.SecurityUtils;

import jakarta.annotation.Resource;
import top.openadexchange.commons.crypto.SecurityKeyUtils;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.dao.DspDao;
import top.openadexchange.dto.DspDto;
import top.openadexchange.model.Dsp;

@Component
public class DspConverter {

    @Resource
    protected EntityCodeService entityCodeService;
    @Resource
    private DspDao dspDao;

    public Dsp from(DspDto dspDto) {
        if (dspDto == null) {
            return null;
        }

        Dsp origDsp = dspDto.getId() == null ? null : dspDao.getById(dspDto.getId());
        Dsp dsp = new Dsp();
        dsp.setId(dspDto.getId());
        dsp.setName(dspDto.getName());
        dsp.setBidEndpoint(dspDto.getBidEndpoint());
        dsp.setWinNoticeEndpoint(dspDto.getWinNoticeEndpoint());
        dsp.setContactName(dspDto.getContactName());
        dsp.setContactPhone(dspDto.getContactPhone());
        dsp.setContactEmail(dspDto.getContactEmail());
        dsp.setStatus(dspDto.getStatus());
        dsp.setQpsLimit(dspDto.getQpsLimit());
        dsp.setTimeoutMs(dspDto.getTimeoutMs());
        dsp.setBrandLogo(dspDto.getBrandLogo());
        dsp.setRtbProtocolType(dspDto.getRtbProtocolType());
        dsp.setDspId(dspDto.getCode());

        // 设置代码和安全密钥
        if (entityCodeService != null && dspDto.getCode() == null) {
            dsp.setDspId(entityCodeService.generateDspCode());
        }

        // 设置用户ID
        dsp.setUserId(SecurityUtils.getUserId());

        // 生成API令牌和加密密钥
        if (origDsp == null) {
            dsp.setToken(SecurityKeyUtils.getApiToken());
            if (dsp.getEncryptionKey() == null) {
                dsp.setEncryptionKey(SecurityKeyUtils.generateEncryptionKey());
            }
            if (dsp.getIntegrityKey() == null) {
                dsp.setIntegrityKey(SecurityKeyUtils.generateIntegrationKey());
            }
        } else {
            if (origDsp.getToken() == null) {
                dsp.setToken(SecurityKeyUtils.getApiToken());
            }
            if (dspDto.getEncryptionKey() != null) {
                dsp.setEncryptionKey(dspDto.getEncryptionKey());
            }
            if (dspDto.getIntegrityKey() != null) {
                dsp.setIntegrityKey(dspDto.getIntegrityKey());
            }
            if (dsp.getEncryptionKey() == null) {
                dsp.setEncryptionKey(SecurityKeyUtils.generateEncryptionKey());
            }
            if (dsp.getIntegrityKey() == null) {
                dsp.setIntegrityKey(SecurityKeyUtils.generateIntegrationKey());
            }
        }

        return dsp;
    }

    public DspDto toDspDto(Dsp dsp) {
        if (dsp == null) {
            return null;
        }

        DspDto dspDto = new DspDto();
        dspDto.setId(dsp.getId());
        dspDto.setName(dsp.getName());
        dspDto.setBidEndpoint(dsp.getBidEndpoint());
        dspDto.setWinNoticeEndpoint(dsp.getWinNoticeEndpoint());
        dspDto.setContactName(dsp.getContactName());
        dspDto.setContactPhone(dsp.getContactPhone());
        dspDto.setContactEmail(dsp.getContactEmail());
        dspDto.setStatus(dsp.getStatus());
        dspDto.setQpsLimit(dsp.getQpsLimit());
        dspDto.setTimeoutMs(dsp.getTimeoutMs());
        dspDto.setBrandLogo(dsp.getBrandLogo());
        dspDto.setCode(dsp.getDspId());
        dspDto.setRtbProtocolType(dsp.getRtbProtocolType());
        dspDto.setCode(dsp.getDspId());
        if (dsp.getRtbProtocolType() == 2) {
            dspDto.setEncryptionKey(dsp.getEncryptionKey());
            dspDto.setIntegrityKey(dsp.getIntegrityKey());
        }

        return dspDto;
    }
}