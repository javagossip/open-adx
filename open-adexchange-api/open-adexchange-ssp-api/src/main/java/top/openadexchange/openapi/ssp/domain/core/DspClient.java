package top.openadexchange.openapi.ssp.domain.core;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.enums.PriceMode;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.application.factory.TrackTokenBuilder;
import top.openadexchange.openapi.ssp.config.OaxEngineProperties;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.openapi.ssp.spi.RtbProtocolInvoker;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.rtb.proto.OaxRtbProto;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;

@Component
@Slf4j
public class DspClient {

    private OaxEngineProperties oaxEngineProperties;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BidResponse bidding(DspAggregate dspAggregate, BidRequest.Builder request) {
        String dspId = dspAggregate.getDsp().getDspId();
        // 1. 获取协议转换扩展点
        RtbProtocolConverter rtbProtocolConverter = OaxSpiFactory.getRtbProtocolConverter(dspId);
        //2. 获取协议调用扩展点
        RtbProtocolInvoker invoker = OaxSpiFactory.getRtbProtocolInvoker(dspId);
        //3. 发起rtb请求调用
        if (request.getTest()) {
            log.info("BidRequest: {}", request);
        }
        Object dspRequest = rtbProtocolConverter.to(dspAggregate.getDsp(), request);
        Object response = invoker.invoke(dspAggregate.getDsp(), dspRequest);
        BidResponse.Builder bidResponse = rtbProtocolConverter.from(dspAggregate.getDsp(), request.build(), response);
        bidResponse.getSeatbidBuilderList().forEach(seatbid -> {
            seatbid.getBidBuilderList().forEach(bid -> {
                resetBidTrackers(dspAggregate, request, bid);
            });
        });
        if (request.getTest()) {
            log.info("dsp {} BidResponse: {}", dspAggregate.getDsp().getName(), bidResponse.toString());
        }
        return bidResponse.build();
    }

    private void resetBidTrackers(DspAggregate dspAggregate, OaxRtbProto.BidRequest.Builder request, Bid.Builder bid) {
        //添加ADX平台自有的曝光监测和点击监测地址，用来记录广告的点击以及曝光数
        String trackingUrl = oaxEngineProperties.getTrackingUrl();
        if (trackingUrl != null && !trackingUrl.isEmpty()) {
            TrackToken trackToken = buildTrackToken(dspAggregate.getDsp(), request, bid, bid.getImpid());
            String impTrackUrl = TrackTokenBuilder.buildImpTrackUrl(trackingUrl, trackToken);
            String clkTrackUrl = TrackTokenBuilder.buildClkTrackUrl(trackingUrl, trackToken);
            bid.addImpTrackers(impTrackUrl);
            bid.addClkTrackers(clkTrackUrl);
        }
    }

    /**
     * 构建跟踪Token
     *
     * @param dsp DSP信息
     * @param bidRequest OAX竞价请求
     * @param bid 竞价响应中的Bid
     * @param impid 曝光ID
     * @return TrackToken
     */
    private TrackToken buildTrackToken(Dsp dsp,
            OaxRtbProto.BidRequest.Builder bidRequest,
            OaxRtbProto.BidResponse.SeatBid.Bid.Builder bid,
            String impid) {
        TrackToken trackToken = new TrackToken();
        trackToken.setReqId(bidRequest.getId());
        trackToken.setImpId(impid);
        // 暂时使用空字符串，后续可以通过tagId查询SiteAdPlacement获取siteId作为publisherId
        trackToken.setPublisherId("");
        trackToken.setAdSotId(bidRequest.getImpList().get(0).getTagid());
        trackToken.setCrid(bid.getCrid());
        // 信和Bid没有advId字段，暂时使用空字符串
        trackToken.setAdvId("");
        trackToken.setDspId(dsp.getDspId());
        trackToken.setTs(System.currentTimeMillis());
        // TODO 设置过期时间为30天后（可根据不同广告类型调整）
        trackToken.setExpireAt(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L);
        trackToken.setUa(bidRequest.getDevice().getUa());
        trackToken.setIp(bidRequest.getDevice().getIp());
        trackToken.setIpv6(bidRequest.getDevice().getIpv6());
        trackToken.setPrice(String.valueOf(bid.getPrice()));
        trackToken.setOs(bidRequest.getDevice().getOs());
        trackToken.setOsv(bidRequest.getDevice().getOsv());
        // 默认使用CPM计费模式
        trackToken.setPriceMode(PriceMode.CPM.name());
        return trackToken;
    }
}
