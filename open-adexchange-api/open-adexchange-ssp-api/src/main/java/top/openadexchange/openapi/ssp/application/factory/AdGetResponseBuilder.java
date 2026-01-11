package top.openadexchange.openapi.ssp.application.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.openapi.ssp.application.dto.AdGetRequest;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse.Ad;
import top.openadexchange.openapi.ssp.application.dto.AdGetResponse.NativeAd;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataRepository;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;

@Component
@Slf4j
public class AdGetResponseBuilder {

    @Resource
    private MetadataRepository metadataRepository;

    public AdGetResponse buildAdGetResponse(AdGetRequest request, Map<String, Bid> bids) {
        AdGetResponse adGetResponse = new AdGetResponse();
        adGetResponse.setId(request.getId());

        List<Ad> ads = new ArrayList<>(bids.size());
        bids.forEach((impid, bid) -> ads.add(buildAd(request, impid, bid)));
        adGetResponse.setAds(ads);
        return adGetResponse;
    }

    private Ad buildAd(AdGetRequest request, String impid, Bid bid) {
        String tagId = request.getTagIdByImpId(impid);
        Ad ad = new Ad();
        ad.setCm(bid.getImpTrackersList());
        ad.setCm(bid.getClkTrackersList());
        ad.setCrid(bid.getCrid());
        ad.setDlk(bid.getDeeplink());
        ad.setAdl(bid.getAppDownloadUrl());
        ad.setLdp(bid.getLdp());
        ad.setCurl(bid.getCreativeUrl());
        ad.setTagid(tagId);
        ad.setBundle(bid.getBundle());
        ad.setNativeAd(buildNativeAd(bid));
        return ad;
    }

    private NativeAd buildNativeAd(Bid bid) {
        BidResponse.NativeAd nativeAd = bid.getNativeAd();
        if (!bid.hasNativeAd()) {
            return null;
        }
        AdGetResponse.NativeAd _nativeAd = new AdGetResponse.NativeAd();
        _nativeAd.setAssets(nativeAd.getAssetsMap());
        return _nativeAd;
    }
}
