package top.openadexchange.openapi.ssp.application.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.commons.StatsUtils;
import top.openadexchange.model.Publisher;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse.Ad;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse.NativeAd;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Builder;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;

@Component
@Slf4j
public class AdGetResponseBuilder {

    @Resource
    private MetadataCacheService metadataCacheService;

    public AdGetResponse buildAdGetResponse(BidRequest.Builder bidRequest,
            AdGetRequest request,
            Map<String, Bid.Builder> bids) {
        if (bids == null || bids.isEmpty()) {
            return null;
        }
        AdGetResponse adGetResponse = new AdGetResponse();
        adGetResponse.setId(request.getId());

        List<Ad> ads = new ArrayList<>(bids.size());
        bids.forEach((impId, bid) -> ads.add(buildAd(bidRequest, request, impId, bid)));
        adGetResponse.setAds(ads);
        return adGetResponse;
    }

    private Ad buildAd(Builder bidRequest, AdGetRequest request, String impid, Bid.Builder bid) {
        Publisher publisher = metadataCacheService.getPublisher(bidRequest.getPublisher().getId());

        String tagId = request.getTagIdByImpId(impid);
        Ad ad = new Ad();
        ad.setImpid(impid);
        ad.setPm(bid.getImpTrackersList());
        ad.setCm(bid.getClkTrackersList());
        ad.setDm(bid.getDownloadTrackersList());
        ad.setDsm(bid.getDownloadCompletedTrackersList());
        ad.setVpm(bid.getPlayTrackersList());
        ad.setVpcm(bid.getPlayCompletedTrackersList());
        ad.setCrid(bid.getCrid());
        ad.setDlk(bid.getDeeplink());
        ad.setAdl(bid.getAppDownloadUrl());
        ad.setLdp(bid.getLdp());
        ad.setCurl(bid.getCreativeUrl());
        ad.setTagid(tagId);
        ad.setBundle(bid.getBundle());
        ad.setNativeAd(buildNativeAd(bid));
        if (publisher != null && publisher.getRevShare() != null) {
            ad.setPrice(StatsUtils.calcMediaRevenue(bid.getPrice(), publisher.getRevShare()));
        }
        return ad;
    }

    private NativeAd buildNativeAd(Bid.Builder bid) {
        BidResponse.NativeAd nativeAd = bid.getNativeAd();
        if (!bid.hasNativeAd()) {
            return null;
        }
        AdGetResponse.NativeAd _nativeAd = new AdGetResponse.NativeAd();
        _nativeAd.setTitle(nativeAd.getTitle());
        _nativeAd.setIcon(nativeAd.getIcon());
        _nativeAd.setDesc(nativeAd.getDesc());
        _nativeAd.setMainImage(nativeAd.getMainImage());
        _nativeAd.setImages(nativeAd.getImagesList());
        _nativeAd.setVideo(nativeAd.getVideo());
        _nativeAd.setAddress(nativeAd.getAddress());
        _nativeAd.setCtaText(nativeAd.getCta());
        _nativeAd.setRating(nativeAd.getRating());
        _nativeAd.setLikes(nativeAd.getLikes());
        _nativeAd.setDownloads(nativeAd.getDownloads());
        _nativeAd.setSponsored(nativeAd.getSponsored());
        _nativeAd.setPrice(nativeAd.getPrice());
        _nativeAd.setSalePrice(nativeAd.getSalePrice());
        _nativeAd.setPhone(nativeAd.getPhone());
        _nativeAd.setDesc2(nativeAd.getDesc2());
        _nativeAd.setDisplayUrl(nativeAd.getDisplayUrl());
        return _nativeAd;
    }
}
