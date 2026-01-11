package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.SecurityUtils;

import jakarta.annotation.Resource;
import top.openadexchange.commons.crypto.SecurityKeyUtils;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.dto.DspDto;
import top.openadexchange.model.Dsp;

@Component
public class DspConverter {

    @Resource
    protected EntityCodeService entityCodeService;

    public Dsp from(DspDto dspDto) {
        if (dspDto == null) {
            return null;
        }

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

        // 设置代码和安全密钥
        if (entityCodeService != null) {
            dsp.setDspId(entityCodeService.generateDspCode());
        }

        // 设置用户ID
        dsp.setUserId(SecurityUtils.getUserId());

        // 生成API令牌和加密密钥
        dsp.setToken(SecurityKeyUtils.getApiToken());
        dsp.setEncryptionKey(SecurityKeyUtils.generateEncryptionKey());
        dsp.setIntegrationKey(SecurityKeyUtils.generateIntegrationKey());

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

        return dspDto;
    }
}