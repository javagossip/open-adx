package top.openadexchange.commons.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class EntityCodeService {

    private static final String AD_PLACEMENT_CODE_PREFIX = "ADP";
    private static final String PUBLISHER_CODE_PREFIX = "PUB";
    public static final String SITE_AD_PLACEMENT_CODE_PREFIX = "SAP";
    public static final String DSP_CODE_PREFIX = "DSP";
    private static final String ADVERTISER_CODE_PREFIX = "ADV";

    @Resource
    private EntityCodeGenerator entityCodeGenerator;

    public String generateAdPlacementCode() {
        return String.format("%s-%s", AD_PLACEMENT_CODE_PREFIX, entityCodeGenerator.generateCode());
    }

    public String generatePublisherCode() {
        return String.format("%s-%s", PUBLISHER_CODE_PREFIX, entityCodeGenerator.generateCode());
    }

    public String generateSiteAdPlacementCode() {
        return String.format("%s-%s", SITE_AD_PLACEMENT_CODE_PREFIX, entityCodeGenerator.generateCode());
    }

    public String generateDspCode() {
        return String.format("%s-%s", DSP_CODE_PREFIX, entityCodeGenerator.generateCode());
    }

    public String generateAdvertiserCode() {
        return String.format("%s-%s", ADVERTISER_CODE_PREFIX, entityCodeGenerator.generateCode());
    }
}
