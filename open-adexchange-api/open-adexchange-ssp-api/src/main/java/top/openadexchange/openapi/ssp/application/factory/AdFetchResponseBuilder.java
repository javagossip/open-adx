package top.openadexchange.openapi.ssp.application.factory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import top.openadexchange.openapi.ssp.application.dto.AdFetchResponse;
import top.openadexchange.openapi.ssp.application.dto.AdFetchResponse.Ad;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;

@Component
@Slf4j
public class AdFetchResponseBuilder {

    public AdFetchResponse buildAdFetchResponse(BidRequest bidRequest, Bid bid) {
        AdFetchResponse adFetchResponse = new AdFetchResponse();
        adFetchResponse.setId(bidRequest.getId());

        List<Ad> ads = new ArrayList<>();
        return adFetchResponse;
    }
}
