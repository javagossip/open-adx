package top.openadexchange.openapi.ssp.application.factory;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import top.openadexchange.constants.enums.AdFormat;
import top.openadexchange.constants.enums.SiteType;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataRepository;
import top.openadexchange.openapi.ssp.domain.gateway.OpenApiSspServices;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.App;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Content;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Device;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Geo;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp.Audio;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp.Banner;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp.NativeSpec;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp.Video;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Site;

@Component
public class BidRequestBuilder {

    @Resource
    private OpenApiSspServices openApiSspServices;
    private MetadataRepository metadataRepository;

    @PostConstruct
    public void init() {
        metadataRepository = openApiSspServices.getCachedMetadataRepository();
    }

    public BidRequest buildBidRequest(AdFetchRequest request) {
        BidRequest.Builder builder = BidRequest.newBuilder();

        builder.setId(request.getId());
        builder.setAt(2);
        builder.setTest(request.isTest());
        request.getImp().forEach(imp -> {
            builder.addImp(buildImp(request.isTest(), imp));
        });
        builder.setApp(buildApp(request));
        builder.setSite(buildSite(request));
        builder.setDevice(buildDevice(request));
        return builder.build();
    }

    private Imp buildImp(boolean isTest, AdFetchRequest.Imp imp) {
        Imp.Builder builder = Imp.newBuilder();
        builder.setId(imp.getId());
        builder.setTagid(imp.getTagid());

        SiteAdPlacement siteAdPlacement = metadataRepository.getSiteAdPlacementByTagId(imp.getTagid());
        AdPlacement adPlacementSpec = metadataRepository.getAdPlacement(siteAdPlacement.getId());
        AdFormat adFormat = AdFormat.valueOf(adPlacementSpec.getAdFormat().toUpperCase());
        if (adFormat == AdFormat.BANNER) {
            builder.setBanner(Banner.newBuilder()
                    .setH(adPlacementSpec.getHeight())
                    .setW(adPlacementSpec.getWidth())
                    .addAllMimes(adPlacementSpec.getMimes())
                    .build());
        } else if (adFormat == AdFormat.NATIVE) {
            builder.setNativeSpec(NativeSpec.newBuilder().addTemplateId(adPlacementSpec.getCode()).build());
        } else if (adFormat == AdFormat.VIDEO) {
            builder.setVideo(Video.newBuilder()
                    .setH(adPlacementSpec.getHeight())
                    .setW(adPlacementSpec.getWidth())
                    .addAllMimes(adPlacementSpec.getMimes())
                    .setMinDuration(adPlacementSpec.getMinDuration())
                    .setMaxDuration(adPlacementSpec.getMaxDuration())
                    .setSkipAfter(adPlacementSpec.getSkipAfter())
                    .setSkipMin(adPlacementSpec.getSkipMin())
                    .setSkippable(adPlacementSpec.getSkippable())
                    .build());
        } else if (adFormat == AdFormat.AUDIO) {
            builder.setAudio(Audio.newBuilder()
                    .addAllMimes(adPlacementSpec.getMimes())
                    .setMinDuration(adPlacementSpec.getMinDuration())
                    .setMaxDuration(adPlacementSpec.getMaxDuration())
                    .build());
        }
        builder.setBidFloorCur("CNY");
        builder.setBidFloor(siteAdPlacement.getFloorPrice());
        if (isTest) {
            builder.setBidFloor(0D);
        }
        return builder.build();
    }

    private Device buildDevice(AdFetchRequest request) {
        AdFetchRequest.Device device = request.getDevice();
        int connectionType = device.getConnectionType() == null ? 0 : device.getConnectionType();
        int deviceType = device.getDeviceType() == null ? 0 : device.getDeviceType();

        Device.Builder builder = Device.newBuilder();
        builder.setUa(request.getDevice().getUa());
        builder.setIp(request.getDevice().getIp());
        builder.setIpv6(request.getDevice().getIpv6());
        builder.setDeviceType(deviceType);
        builder.setConnectionType(connectionType);
        builder.setIfa(request.getDevice().getIfa());
        builder.setDidmd5(request.getDevice().getDidmd5());
        builder.setAdid(request.getDevice().getAdid());
        builder.setMac(request.getDevice().getMac());
        builder.setMacmd5(device.getMacmd5());
        builder.setOs(device.getOs());
        builder.setOsv(device.getOsv());
        builder.setCarrier(device.getCarrier());
        builder.setH(device.getH() == null ? 0 : device.getH());
        builder.setW(device.getW() == null ? 0 : device.getW());
        builder.setModel(device.getModel());
        builder.setMake(device.getMake());
        if (device.getGeo() != null) {
            builder.setGeo(Geo.newBuilder()
                    .setLat(device.getGeo().getLat() == null ? 0F : device.getGeo().getLat())
                    .setLon(device.getGeo().getLon() == null ? 0F : device.getGeo().getLon())
                    .build());
        }
        return builder.build();
    }

    private Site buildSite(AdFetchRequest request) {
        //获取任何一个imp对应的广告位编码，因为无论哪个广告位对应的site/app都是同一个
        top.openadexchange.model.Site site = getSite(request);

        if (SiteType.WEBSITE != SiteType.fromValue(site.getSiteType())) {
            return Site.newBuilder().build();
        }
        AdFetchRequest.Site reqSite = request.getSite();
        return Site.newBuilder()
                .setDomain(site.getDomain())
                .addAllCat(List.of(StringUtils.tokenizeToStringArray(site.getCats(), ",")))
                .setKeywords(site.getKeywords())
                .setName(site.getName())
                .setContent(buildSiteOrAppContent(reqSite != null ? reqSite.getContent() : null))
                .build();
    }

    private top.openadexchange.model.Site getSite(AdFetchRequest request) {
        String tagId = request.getImp().getFirst().getTagid();
        SiteAdPlacement siteAdPlacement = metadataRepository.getSiteAdPlacementByTagId(tagId);
        Long siteId = siteAdPlacement.getSiteId();
        top.openadexchange.model.Site site = metadataRepository.getSite(siteId);
        return site;
    }

    private Content buildSiteOrAppContent(AdFetchRequest.Content content) {
        if (content == null) {
            return Content.newBuilder().build();
        }
        return Content.newBuilder().setTitle(content.getTitle()).setKeywords(content.getKeywords()).build();
    }

    private App buildApp(AdFetchRequest request) {
        top.openadexchange.model.Site site = getSite(request);
        if (SiteType.APP != SiteType.fromValue(site.getSiteType())) {
            return App.newBuilder().build();
        }
        AdFetchRequest.App reqApp = request.getApp();
        return App.newBuilder()
                .setBundle(site.getAppBundle())
                .setId(site.getAppId())
                .setName(site.getName())
                .setKeywords(site.getKeywords())
                .addAllCat(List.of(StringUtils.tokenizeToStringArray(site.getCats(), ",")))
                .setVer(reqApp.getVer())
                .setContent(buildSiteOrAppContent(reqApp != null ? reqApp.getContent() : null))
                .build();
    }
}
