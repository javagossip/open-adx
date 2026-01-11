package top.openadexchange.mos.application.converter;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ruoyi.common.utils.SecurityUtils;

import jakarta.annotation.Resource;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.constants.enums.AdFormat;
import top.openadexchange.dto.AdPlacementDto;
import top.openadexchange.dto.adspecification.AudioAdSpecificationDto;
import top.openadexchange.dto.adspecification.BannerAdSpecificationDto;
import top.openadexchange.dto.adspecification.VideoAdSpecificationDto;
import top.openadexchange.model.AdPlacement;

@Component
public class AdPlacementConverter {

    @Resource
    private EntityCodeService entityCodeService;

    public AdPlacement from(AdPlacementDto adPlacementDto) {
        if (adPlacementDto == null) {
            return null;
        }

        AdPlacement adPlacement = new AdPlacement();
        adPlacement.setId(adPlacementDto.getId());
        adPlacement.setName(adPlacementDto.getName());
        adPlacement.setStatus(adPlacementDto.getStatus());

        // 设置code和userId
        if (entityCodeService != null) {
            if (adPlacementDto.getCode() == null) {
                adPlacement.setCode(entityCodeService.generateAdPlacementCode());
            }
        }

        // 处理广告格式和相应属性
        if (adPlacementDto.getAdFormat() != null) {
            adPlacement.setAdFormat(adPlacementDto.getAdFormat().name());

            switch (adPlacementDto.getAdFormat()) {
                case BANNER:
                    if (adPlacementDto.getBanner() != null) {
                        adPlacement.setWidth(adPlacementDto.getBanner().getWidth());
                        adPlacement.setHeight(adPlacementDto.getBanner().getHeight());
                    }
                    break;
                case VIDEO:
                    if (adPlacementDto.getVideo() != null) {
                        adPlacement.setWidth(adPlacementDto.getVideo().getWidth());
                        adPlacement.setHeight(adPlacementDto.getVideo().getHeight());
                        adPlacement.setMinDuration(adPlacementDto.getVideo().getMinDuration());
                        adPlacement.setMaxDuration(adPlacementDto.getVideo().getMaxDuration());
                        adPlacement.setSkippable(adPlacementDto.getVideo().isSkippable());
                        adPlacement.setSkipAfter(adPlacementDto.getVideo().getSkipAfter());
                        adPlacement.setSkipMin(adPlacementDto.getVideo().getSkipMin());
                        adPlacement.setMimes(adPlacementDto.getVideo().getMimes());
                    }
                    break;
                case AUDIO:
                    if (adPlacementDto.getAudio() != null) {
                        adPlacement.setMinDuration(adPlacementDto.getAudio().getMinDuration());
                        adPlacement.setMaxDuration(adPlacementDto.getAudio().getMaxDuration());
                        adPlacement.setSkippable(adPlacementDto.getAudio().isSkippable());
                        adPlacement.setSkipAfter(adPlacementDto.getAudio().getSkipAfter());
                        adPlacement.setSkipMin(adPlacementDto.getAudio().getSkipMin());
                        adPlacement.setMimes(adPlacementDto.getAudio().getMimes());
                    }
                    break;
                case NATIVE:
                    // Native广告格式的属性主要在NativeAsset中，不在AdPlacement本身
                    break;
            }
        }
        adPlacement.setUserId(SecurityUtils.getUserId());
        return adPlacement;
    }

    public AdPlacementDto toAdPlacementDto(AdPlacement adPlacement) {
        if (adPlacement == null) {
            return null;
        }

        AdPlacementDto adPlacementDto = new AdPlacementDto();
        adPlacementDto.setId(adPlacement.getId());
        adPlacementDto.setName(adPlacement.getName());
        adPlacementDto.setCode(adPlacement.getCode());
        adPlacementDto.setStatus(adPlacement.getStatus());

        // 处理广告格式
        if (adPlacement.getAdFormat() != null) {
            try {
                adPlacementDto.setAdFormat(AdFormat.valueOf(adPlacement.getAdFormat()));
            } catch (IllegalArgumentException e) {
                // 如果枚举值不匹配，则保持为null
            }
        }
        Assert.notNull(adPlacementDto.getAdFormat(), "AdFormat cannot be null");
        // 根据广告格式填充相应的属性
        switch (adPlacementDto.getAdFormat()) {
            case BANNER:
                if (adPlacement.getWidth() != null || adPlacement.getHeight() != null) {
                    BannerAdSpecificationDto banner = new BannerAdSpecificationDto();
                    banner.setWidth(adPlacement.getWidth() != null ? adPlacement.getWidth() : 0);
                    banner.setHeight(adPlacement.getHeight() != null ? adPlacement.getHeight() : 0);
                    adPlacementDto.setBanner(banner);
                }
                break;
            case VIDEO:
                if (adPlacement.getWidth() != null || adPlacement.getHeight() != null ||
                        adPlacement.getMinDuration() != null || adPlacement.getMaxDuration() != null) {
                    VideoAdSpecificationDto video = new VideoAdSpecificationDto();
                    video.setWidth(adPlacement.getWidth() != null ? adPlacement.getWidth() : 0);
                    video.setHeight(adPlacement.getHeight() != null ? adPlacement.getHeight() : 0);
                    video.setMinDuration(adPlacement.getMinDuration() != null ? adPlacement.getMinDuration() : 0);
                    video.setMaxDuration(adPlacement.getMaxDuration() != null ? adPlacement.getMaxDuration() : 0);
                    video.setSkippable(adPlacement.getSkippable() != null ? adPlacement.getSkippable() : false);
                    video.setSkipAfter(adPlacement.getSkipAfter() != null ? adPlacement.getSkipAfter() : 0);
                    video.setSkipMin(adPlacement.getSkipMin() != null ? adPlacement.getSkipMin() : 0);
                    video.setMimes(adPlacement.getMimes());
                    adPlacementDto.setVideo(video);
                }
                break;
            case AUDIO:
                if (adPlacement.getMinDuration() != null || adPlacement.getMaxDuration() != null) {
                    AudioAdSpecificationDto audio = new AudioAdSpecificationDto();
                    audio.setMinDuration(adPlacement.getMinDuration() != null ? adPlacement.getMinDuration() : 0);
                    audio.setMaxDuration(adPlacement.getMaxDuration() != null ? adPlacement.getMaxDuration() : 0);
                    audio.setSkippable(adPlacement.getSkippable() != null ? adPlacement.getSkippable() : false);
                    audio.setSkipAfter(adPlacement.getSkipAfter() != null ? adPlacement.getSkipAfter() : 0);
                    audio.setSkipMin(adPlacement.getSkipMin() != null ? adPlacement.getSkipMin() : 0);
                    audio.setMimes(adPlacement.getMimes());
                    adPlacementDto.setAudio(audio);
                }
                break;
            case NATIVE:
                // 对于Native格式，主要属性在NativeAsset中
                break;
        }
        return adPlacementDto;
    }
}