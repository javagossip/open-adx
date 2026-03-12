package top.openadexchange.openapi.ssp.application.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import org.springframework.util.StringUtils;

import top.openadexchange.domain.entity.SiteAdPlacementAggregate;
import top.openadexchange.model.Site;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogSamplingConfig;
import top.openadexchange.oax.model.proto.OaxModelsProto.LogType;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.utils.LogSamplingUtils;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Builder;

@Service
public class LogSamplingConfigService {

    @Resource
    private OaxEngineServices oaxEngineServices;
    @Resource
    private MetadataCacheService metadataCacheService;

    public int getLogSamplingRate(LogType logType, Integer mediaId, Integer adSlotId, Integer dspId) {
        IndexService indexService = oaxEngineServices.getIndexService();
        Integer lscId = indexService.getLscId(logType, mediaId, adSlotId, dspId);

        if (lscId == null) {
            return 0;
        }
        LogSamplingConfig lsc = metadataCacheService.getLogSamplingConfig(lscId);
        return lsc.getSamplingRate();
    }

    public boolean shouldLog(LogType logType, AdGetRequest request) {
        Assert.notNull(request, "request can not be null");
        Assert.hasText(request.getId(), "requestId can not be null");
        Assert.notEmpty(request.getImp(), "imp can not be empty");

        String requestId = request.getId();
        AdGetRequest.Imp firstImp = request.getImp().get(0);
        String tagId = firstImp.getTagid();
        Assert.hasText(tagId, "tagId can not be empty");

        return shouldLog(logType, requestId, tagId, null);
    }

    /**
     * 判断当前请求是否需要打日志
     *
     * @param logType 日志类型
     * @param tagId 广告位ID
     * @param dspId DSP ID
     * @return 是否需要打日志
     */
    public boolean shouldLog(LogType logType, String requestId, String tagId, Integer dspId) {
        Assert.hasText(requestId, "requestId can not be empty");
        Assert.hasText(tagId, "tagId can not be empty");

        SiteAdPlacementAggregate siteAdPlacementAggregate = metadataCacheService.getSiteAdPlacementByTagId(tagId);
        Integer adSlotId = null;
        Integer mediaId = null;
        if (siteAdPlacementAggregate != null && siteAdPlacementAggregate.getSiteAdPlacement() != null) {
            adSlotId = siteAdPlacementAggregate.getSiteAdPlacement().getId();
            Integer siteId = siteAdPlacementAggregate.getSiteAdPlacement().getSiteId();
            if (siteId != null) {
                Site site = metadataCacheService.getSite(siteId);
                if (site != null) {
                    mediaId = site.getPublisherId();
                }
            }
        }
        int samplingRate = getLogSamplingRate(logType, mediaId, adSlotId, null);
        return LogSamplingUtils.isSampled(requestId, samplingRate);
    }

    public boolean shouldLog(LogType logType, BidRequest.Builder bidRequest, Integer dspId) {
        String requestId = bidRequest.getId();
        String tagId = bidRequest.getImpList().getFirst().getTagid();
        return shouldLog(logType, requestId, tagId, null);
    }
}
