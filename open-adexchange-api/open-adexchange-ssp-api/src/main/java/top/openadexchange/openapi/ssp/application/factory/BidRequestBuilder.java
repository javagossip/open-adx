package top.openadexchange.openapi.ssp.application.factory;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import top.openadexchange.constants.enums.AdFormat;
import top.openadexchange.constants.enums.SiteType;
import top.openadexchange.model.AdPlacement;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataRepository;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
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
    private OaxEngineServices oaxEngineServices;

    public BidRequest buildBidRequest(AdGetRequest request) {
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

    private Imp buildImp(boolean isTest, AdGetRequest.Imp imp) {
        Imp.Builder builder = Imp.newBuilder();
        builder.setId(imp.getId());
        builder.setTagid(imp.getTagid());

        MetadataRepository metadataRepository = oaxEngineServices.getCachedMetadataRepository();
        SiteAdPlacement siteAdPlacement = metadataRepository.getSiteAdPlacementByTagId(imp.getTagid());
        AdPlacement adPlacementSpec = metadataRepository.getAdPlacement(siteAdPlacement.getAdPlacementId());
        AdFormat adFormat = AdFormat.valueOf(adPlacementSpec.getAdFormat().toUpperCase());
        if (adFormat == AdFormat.BANNER) {
            builder.setBanner(Banner.newBuilder()
                    .setH(adPlacementSpec.getHeight())
                    .setW(adPlacementSpec.getWidth())
                    .addAllMimes(
                            adPlacementSpec.getMimes() == null ? Collections.emptyList() : adPlacementSpec.getMimes()));
        } else if (adFormat == AdFormat.NATIVE) {
            builder.setNativeSpec(NativeSpec.newBuilder().addTemplateId(adPlacementSpec.getCode()).build());
        } else if (adFormat == AdFormat.VIDEO) {
            Video.Builder videoBuilder = Video.newBuilder();
            if (adPlacementSpec.getWidth() != null) {
                videoBuilder.setW(adPlacementSpec.getWidth());
            }
            if (adPlacementSpec.getHeight() != null) {
                videoBuilder.setH(adPlacementSpec.getHeight());
            }
            if (adPlacementSpec.getMimes() != null) {
                videoBuilder.addAllMimes(adPlacementSpec.getMimes());
            }
            videoBuilder.setMaxDuration(adPlacementSpec.getMaxDuration())
                    .setMinDuration(adPlacementSpec.getMinDuration());
            if (adPlacementSpec.getSkippable() != null) {
                videoBuilder.setSkippable(adPlacementSpec.getSkippable());
            }
            if (adPlacementSpec.getSkipAfter() != null) {
                videoBuilder.setSkipAfter(adPlacementSpec.getSkipAfter());
            }
            if (adPlacementSpec.getSkipMin() != null) {
                videoBuilder.setSkipMin(adPlacementSpec.getSkipMin());
            }
            builder.setVideo(videoBuilder);
        } else if (adFormat == AdFormat.AUDIO) {
            builder.setAudio(Audio.newBuilder()
                    .addAllMimes(
                            adPlacementSpec.getMimes() == null ? Collections.emptyList() : adPlacementSpec.getMimes())
                    .setMinDuration(adPlacementSpec.getMinDuration())
                    .setMaxDuration(adPlacementSpec.getMaxDuration())
                    .build());
        }
        builder.setBidFloorCur("CNY");
        builder.setBidFloor(siteAdPlacement.getFloorPrice());
        if (isTest) {
            builder.setBidFloor(0);
        }
        return builder.build();
    }

    private Device buildDevice(AdGetRequest request) {
        AdGetRequest.Device device = request.getDevice();
        int connectionType = device.getConnectionType() == null ? 0 : device.getConnectionType();
        int deviceType = device.getDeviceType() == null ? 0 : device.getDeviceType();

        //AdGetRequest.Device device = request.getDevice();

        Device.Builder builder = Device.newBuilder();
        if (device.getUa() != null) {
            builder.setUa(device.getUa());
        }
        if (device.getIp() != null) {
            builder.setIp(device.getIp());
        }
        if (device.getIpv6() != null) {
            builder.setIpv6(device.getIpv6());
        }
        builder.setDeviceType(deviceType);
        builder.setConnectionType(connectionType);
        if (device.getIfa() != null) {
            builder.setIfa(device.getIfa());
        }
        if (device.getDidmd5() != null) {
            builder.setDidmd5(device.getDidmd5());
        }
        if (device.getAdid() != null) {
            builder.setAdid(device.getAdid());
        }
        if (device.getMac() != null) {
            builder.setMac(device.getMac());
        }
        if (device.getMacmd5() != null) {
            builder.setMacmd5(device.getMacmd5());
        }
        if (device.getOs() != null) {
            builder.setOs(device.getOs());
        }
        if (device.getOsv() != null) {
            builder.setOsv(device.getOsv());
        }
        if (device.getCarrier() != null) {
            builder.setCarrier(device.getCarrier());
        }
        builder.setH(device.getH() == null ? 0 : device.getH());
        builder.setW(device.getW() == null ? 0 : device.getW());
        if (device.getModel() != null) {
            builder.setModel(device.getModel());
        }
        if (device.getMake() != null) {
            builder.setMake(device.getMake());
        }
        if (device.getGeo() != null) {
            builder.setGeo(Geo.newBuilder()
                    .setLat(device.getGeo().getLat() == null ? 0F : device.getGeo().getLat())
                    .setLon(device.getGeo().getLon() == null ? 0F : device.getGeo().getLon())
                    .build());
        }
        return builder.build();
    }

    private Site buildSite(AdGetRequest request) {
        //获取任何一个imp对应的广告位编码，因为无论哪个广告位对应的site/app都是同一个
        top.openadexchange.model.Site site = getSite(request);

        if (SiteType.WEBSITE != SiteType.fromValue(site.getSiteType())) {
            return Site.newBuilder().build();
        }
        AdGetRequest.Site reqSite = request.getSite();
        Site.Builder builder = Site.newBuilder();
        if (site.getDomain() != null) {
            builder.setDomain(site.getDomain());
        }
        if (site.getCats() != null) {
            builder.addAllCat(List.of(StringUtils.tokenizeToStringArray(site.getCats(), ",")));
        }
        if (site.getName() != null) {
            builder.setName(site.getName());
        }
        if (site.getKeywords() != null) {
            builder.setKeywords(site.getKeywords());
        }
        return builder.setContent(buildSiteOrAppContent(reqSite != null ? reqSite.getContent() : null)).build();
    }

    private top.openadexchange.model.Site getSite(AdGetRequest request) {
        MetadataRepository metadataRepository = oaxEngineServices.getCachedMetadataRepository();
        String tagId = request.getImp().getFirst().getTagid();
        SiteAdPlacement siteAdPlacement = metadataRepository.getSiteAdPlacementByTagId(tagId);
        Long siteId = siteAdPlacement.getSiteId();
        top.openadexchange.model.Site site = metadataRepository.getSite(siteId);
        return site;
    }

    private Content buildSiteOrAppContent(AdGetRequest.Content content) {
        if (content == null) {
            return Content.newBuilder().build();
        }
        Content.Builder builder = Content.newBuilder();
        if (content.getTitle() != null) {
            builder.setTitle(content.getTitle());
        }
        if (content.getKeywords() != null) {
            builder.setKeywords(content.getKeywords());
        }
        return builder.build();
    }

    private App buildApp(AdGetRequest request) {
        top.openadexchange.model.Site site = getSite(request);
        if (SiteType.APP != SiteType.fromValue(site.getSiteType())) {
            return App.newBuilder().build();
        }
        AdGetRequest.App reqApp = request.getApp();
        if (reqApp == null) {
            return App.newBuilder().build();
        }
        return App.newBuilder()
                .setBundle(site.getAppBundle() == null ? "" : site.getAppBundle())
                .setId(site.getAppId() == null ? "" : site.getAppId())
                .setName(site.getName() == null ? "" : site.getName())
                .setKeywords(site.getKeywords() == null ? "" : site.getKeywords())
                .addAllCat(List.of(StringUtils.tokenizeToStringArray(site.getCats(), ",")))
                .setVer(reqApp.getVer() == null ? "" : reqApp.getVer())
                .setContent(buildSiteOrAppContent(reqApp != null ? reqApp.getContent() : null))
                .build();
    }
}
