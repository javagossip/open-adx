package top.openadexchange.openapi.dsp.application.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import org.springframework.util.Assert;

import top.openadexchange.commons.AssertUtils;
import top.openadexchange.commons.service.EntityCodeService;
import top.openadexchange.constants.enums.CreativeType;
import top.openadexchange.dao.AdPlacementDao;
import top.openadexchange.dao.NativeAssetDao;
import top.openadexchange.domain.entity.CreativeAggregate;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.Creative;
import top.openadexchange.model.CreativeAsset;
import top.openadexchange.model.NativeAsset;
import top.openadexchange.openapi.dsp.application.dto.AuditResult;
import top.openadexchange.openapi.dsp.application.dto.CreativeAuditResultDto;
import top.openadexchange.openapi.dsp.application.dto.CreativeDto;
import top.openadexchange.openapi.dsp.application.dto.NativeAdDto;
import top.openadexchange.openapi.dsp.commons.ApiErrorCode;
import top.openadexchange.openapi.dsp.commons.AuditStatus;

@Component
public class CreativeConverter {

    @Resource
    private NativeAssetDao nativeAssetDao;
    @Resource
    private AdPlacementDao adPlacementDao;
    @Resource
    private EntityCodeService entityCodeService;

    public CreativeAggregate from(CreativeDto creativeDto) {
        CreativeAggregate creativeAggregate = new CreativeAggregate();
        creativeAggregate.setCreative(toCreative(creativeDto));
        creativeAggregate.setCreativeAssets(toCreativeAssets(creativeDto));

        return creativeAggregate;
    }

    private Creative toCreative(CreativeDto creativeDto) {
        Creative creative = new Creative();
        creative.setDspCreativeId(creativeDto.getCreativeId());
        creative.setDspAdvertiserId(creativeDto.getAdvertiserId());
        creative.setName(creativeDto.getName());
        creative.setCreativeType(creativeDto.getCreativeType());
        creative.setCreativeUrl(creativeDto.getCreativeUrl());
        creative.setLandingUrl(creativeDto.getLandingPage());
        creative.setStatus(1); // 默认启用
        creative.setCreatedAt(LocalDateTime.now());
        creative.setUpdatedAt(LocalDateTime.now());
        creative.setAuditStatus(AuditStatus.PENDING.name());
        creative.setDuration(creativeDto.getDuration());
        creative.setWidth(creativeDto.getWidth());
        creative.setHeight(creativeDto.getHeight());
        creative.setMimes(creativeDto.getMimes());
        creative.setCreativeId(entityCodeService.generateCreativeCode());

        CreativeType creativeType;
        try {
            creativeType = CreativeType.valueOf(creativeDto.getCreativeType());
        } catch (IllegalArgumentException ex) {
            throw ApiErrorCode.INVALID_CREATIVE_TYPE.toException();
        }

        if (creativeType == CreativeType.NATIVE) {
            NativeAdDto nativeAd = creativeDto.getNativeAd();
            creative.setAdxTemplateId(nativeAd.getTemplateCode());
        }
        return creative;
    }

    private List<CreativeAsset> toCreativeAssets(CreativeDto creativeDto) {
        if (!creativeDto.getCreativeType().equals(CreativeType.NATIVE.name())) {
            return null;
        }
        if (creativeDto.getNativeAd() == null) {
            return null;
        }
        String templateCode = creativeDto.getNativeAd().getTemplateCode();
        AdPlacement adPlacement = adPlacementDao.getByTemplateCode(templateCode);
        Assert.notNull(adPlacement, "Ad placement not found for template code: " + templateCode);

        List<NativeAsset> nativeAssets = nativeAssetDao.listByAdPlacementId(adPlacement.getId());
        //<assetKey,NativeAsset>
        Map<String, NativeAsset> nativeAssetMap = nativeAssets.stream()
                .collect(Collectors.toMap(NativeAsset::getAssetKey, Function.identity(), (a, b) -> a));

        // 创建CreativeAsset列表
        List<CreativeAsset> creativeAssets = new ArrayList<>();

        // 如果是原生广告类型，需要处理assets
        Map<String, String> assets = creativeDto.getNativeAd().getAssets();

        for (Map.Entry<String, String> entry : assets.entrySet()) {
            NativeAsset nativeAsset = nativeAssetMap.get(entry.getKey());
            if (nativeAsset == null) {
                continue;
            }
            CreativeAsset creativeAsset = new CreativeAsset();
            creativeAsset.setAssetKey(entry.getKey());
            creativeAsset.setAssetValue(entry.getValue());
            creativeAsset.setDataAssetType(nativeAsset.getDataAssetType());
            creativeAsset.setFormat(nativeAsset.getFormat());
            creativeAsset.setMimeType(nativeAsset.getMimeTypes());
            creativeAsset.setRequired(nativeAsset.getRequired());
            creativeAssets.add(creativeAsset);
        }
        return creativeAssets;
    }

    public CreativeAuditResultDto toCreativeAuditResultDto(Creative creative) {
        CreativeAuditResultDto creativeAuditResultDto = new CreativeAuditResultDto();
        creativeAuditResultDto.setCreativeId(creative.getDspCreativeId());
        creativeAuditResultDto.setAuditResult(new AuditResult(creative.getAuditStatus(),
                creative.getAuditReason(),
                creative.getAuditTime() == null
                        ? null
                        : creative.getAuditTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        return creativeAuditResultDto;
    }
}