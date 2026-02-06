package top.openadexchange.openapi.ssp.domain.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.constants.enums.AuctionType;
import top.openadexchange.constants.enums.PriceMode;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.dto.TrackToken;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.Publisher;
import top.openadexchange.openapi.ssp.application.factory.IndexKeysBuilder;
import top.openadexchange.openapi.ssp.application.factory.TrackTokenBuilder;
import top.openadexchange.openapi.ssp.application.service.MetricsCollector;
import top.openadexchange.openapi.ssp.config.OaxEngineProperties;
import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactories;
import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactory;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataCacheService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataRepository;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;
import top.openadexchange.openapi.ssp.spi.MacroContextBuilder;
import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;
import top.openadexchange.rtb.proto.OaxRtbProto;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid.Builder;

@Service
@Slf4j
public class AdExchangeEngine {

    private static final long DELTA = 1; //1分
    private static final Comparator<DspBid> BID_PRICE_DESCENDING = (a, b) -> Long.compare(b.getPrice(), a.getPrice());
    @Resource
    private DspClient dspClient;
    @Resource
    private OaxEngineServices oaxEngineServices;
    @Resource
    private OaxEngineProperties oaxEngineProperties;
    @Resource
    private IndexKeysBuilder indexKeysBuilder;
    @Resource
    private ExecutorFactories executorFactories;
    @Resource
    private MetadataCacheService metadataCacheService;
    @Resource
    private MetricsCollector metricsCollector;

    public Map<String, Bid.Builder> bidding(BidRequest.Builder request) {
        // 1. 获取所有 DSP 的响应 (并发逻辑同前)
        for (Imp imp : request.getImpList()) {
            metricsCollector.incrementAdSlotReqs(imp.getTagid());
        }
        Map<String, Imp> impFloorMap =
                request.getImpList().stream().collect(Collectors.toMap(Imp::getId, Function.identity(), (a, b) -> a));
        Map<String, List<DspBid>> validImpBids = fetchAllBids(request, impFloorMap);
        if (validImpBids == null || validImpBids.isEmpty()) {
            return null;
        }
        Map<String, Bid.Builder> winnerBids = new HashMap<>();
        validImpBids.forEach((impId, bids) -> winnerBids.put(impId,
                selectWinBid(request, bids, impFloorMap.get(impId))));
        return winnerBids;
    }

    private Bid.Builder selectWinBid(BidRequest.Builder request, List<DspBid> bids, Imp imp) {
        // 3. 执行二价计费算法
        long impFloor = imp.getBidFloor();
        bids.sort(BID_PRICE_DESCENDING);
        DspBid winner = bids.get(0);

        metricsCollector.incrementDspWins(winner.getDspId());
        metricsCollector.incrementAdSlotWins(imp.getTagid());
        //如果获胜dsp的出价类型是First Price，则直接返回中标者的出价
        Dsp winDsp = winner.getDsp();
        if (winDsp.getAt() == AuctionType.FIRST_PRICE.getValue()) {
            log.info("竞价完成，中标者: {}, 一价结算，结算价：{}", winDsp.getName(), winner.getBid().getPrice());
            replaceMacros(winner.getBid(), winner);
            resetBidTrackers(request, winDsp, winner.getBid(), imp);
            return winner.getBid();
        }

        long settlementPrice;
        if (bids.size() > 1) {
            // 有多个竞标者，取第二名价格 + DELTA
            long secondPrice = bids.get(1).getBid().getPrice();
            settlementPrice = secondPrice + DELTA;

            // 兜底：结算价不能超过中标者自己的出价
            settlementPrice = Math.min(settlementPrice, winner.getBid().getPrice());
        } else {
            // 只有一个竞标者，按底价结算
            settlementPrice = impFloor;
        }

        // 4. 设置最终结算价格并返回
        Bid.Builder builder = winner.getBid();
        //5. 对WinNotice url以及点击/曝光监测地址进行宏替换处理
        //6. 发送WinNotice请求
        log.info("竞价完成，中标者: {}, 原始出价: {}, 最终结算价: {}",
                winDsp.getName(),
                winner.getBid().getPrice(),
                settlementPrice);
        builder.setPrice(settlementPrice);
        resetBidTrackers(request, winner.getDsp(), builder, imp);
        replaceMacros(builder, winner);
        return builder;
    }

    private void replaceMacros(Builder builder, DspBid dspBid) {
        MacroContextBuilder macroContextBuilder = OaxSpiFactory.getMacroContextBuilder(dspBid.getDspId());
        MacroContext macroContext = macroContextBuilder.build(dspBid);

        MacroProcessor macroProcessor = OaxSpiFactory.getMacroProcessor(dspBid.getDspId());
        builder.setNurl(macroProcessor.process(builder.getNurl(), macroContext));

        List<String> origImpTrackingUrls = builder.getImpTrackersList();
        List<String> origClkTrackingUrls = builder.getClkTrackersList();

        List<String> impTrackingUrls = origImpTrackingUrls.stream()
                .map(url -> macroProcessor.process(url, macroContext))
                .collect(Collectors.toList());
        List<String> clkTrackingUrls = origClkTrackingUrls.stream()
                .map(url -> macroProcessor.process(url, macroContext))
                .collect(Collectors.toList());

        builder.clearImpTrackers().addAllImpTrackers(impTrackingUrls);
        builder.clearClkTrackers().addAllClkTrackers(clkTrackingUrls);
    }

    //返回按照impid进行分组的竞价结果
    private Map<String, List<DspBid>> fetchAllBids(BidRequest.Builder request, /*<impid, floorPrice>*/
            Map<String, Imp> impFloorMap) {
        IndexService indexService = oaxEngineServices.getIndexService();
        MetadataRepository metadataRepository = oaxEngineServices.getCachedMetadataRepository();

        IndexKeys indexKeys = indexKeysBuilder.buildIndexKeys(request);
        if (request.getTest()) {
            log.info("test request: {}, indexKeys: {}", request, indexKeys);
        }
        //从索引库中查询匹配当前广告流量的dsp列表
        List<Integer> matchDspIds = indexService.searchDsps(indexKeys);
        if (request.getTest()) {
            log.info("test request, matchDspIds: {}", matchDspIds);
        }
        if (matchDspIds.isEmpty()) {
            log.info("no match dsp, request id: {}", request.getId());
            return null;
        }
        List<DspAggregate> matchDsps = metadataRepository.getDspByIds(matchDspIds);
        // 1. 发起并发 RTB 请求
        ExecutorFactory executorFactory = executorFactories.getExecutorFactory();
        ExecutorService executor = executorFactory.getExecutor();

        List<Callable<DspBidResponse>> tasks = matchDsps.stream()
                .map(dspAggregate -> (Callable<DspBidResponse>) () -> DspBidResponse.of(dspAggregate.getDsp(),
                        dspClient.bidding(dspAggregate, request)))
                .collect(Collectors.toList());

        try {
            //向符合条件的 dsp发起实时竞价请求, 并获得竞价响应
            List<Future<DspBidResponse>> futures =
                    executor.invokeAll(tasks, oaxEngineProperties.getDspCallTimeout(), TimeUnit.MILLISECONDS);
            List<DspBidResponse> dspBidResponses = futures.stream().map(future -> {
                try {
                    return future.isDone() ? future.get() : null;
                } catch (Exception ex) {
                    log.error("invokeAll error", ex);
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());

            Map<String, List<DspBid>> validImpBids = new HashMap<>();
            for (DspBidResponse dspBidResponse : dspBidResponses) {
                Dsp dsp = dspBidResponse.getDsp();
                List<SeatBid.Builder> seatBids = dspBidResponse.getSeatbidList();
                for (SeatBid.Builder seatBid : seatBids) {
                    for (Bid.Builder bid : seatBid.getBidBuilderList()) {
                        Imp imp = impFloorMap.get(bid.getImpid());
                        Long floor = imp.getBidFloor();
                        if (floor != null && bid.getPrice() > floor) {
                            validImpBids.computeIfAbsent(bid.getImpid(), k -> new ArrayList<>())
                                    .add(new DspBid(dsp, bid));
                        }
                    }
                }
            }
            return validImpBids;
        } catch (InterruptedException ex) {
            log.error("invokeAll error", ex);
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    public static class DspBid {

        private Dsp dsp;
        private Bid.Builder bid;

        public String getImpid() {
            return bid.getImpid();
        }

        public long getPrice() {
            return bid.getPrice();
        }

        public String getDspId() {
            return dsp.getDspId();
        }
    }

    @Data
    public static class DspBidResponse {

        private Dsp dsp;
        private BidResponse.Builder bidResponse;

        private DspBidResponse(Dsp dsp, BidResponse.Builder bidResponse) {
            this.dsp = dsp;
            this.bidResponse = bidResponse;
        }

        public static DspBidResponse of(Dsp dsp, BidResponse.Builder bidResponse) {
            return new DspBidResponse(dsp, bidResponse);
        }

        public List<SeatBid.Builder> getSeatbidList() {
            return bidResponse.getSeatbidBuilderList();
        }
    }

    private void resetBidTrackers(BidRequest.Builder request, Dsp dsp, Bid.Builder bid, Imp imp) {
        //添加ADX平台自有的曝光监测和点击监测地址，用来记录广告的点击以及曝光数
        String trackingUrl = oaxEngineProperties.getTrackingUrl();
        if (trackingUrl != null && !trackingUrl.isEmpty()) {
            TrackToken trackToken = buildTrackToken(dsp, request, bid, imp);
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
     * @param request OAX竞价请求
     * @param bid 竞价响应中的Bid
     * @param imp 曝光ID
     * @return TrackToken
     */
    private TrackToken buildTrackToken(Dsp dsp,
            BidRequest.Builder request,
            OaxRtbProto.BidResponse.SeatBid.Bid.Builder bid,
            Imp imp) {
        long publisherId = request.getPublisher().getId();
        TrackToken trackToken = new TrackToken();
        trackToken.setReqId(request.getId());
        trackToken.setImpId(imp.getId());
        // 暂时使用空字符串，后续可以通过tagId查询SiteAdPlacement获取siteId作为publisherId
        trackToken.setPublisherId(String.valueOf(publisherId));
        trackToken.setAdSlotId(imp.getTagid());
        trackToken.setCrid(bid.getCrid());
        // 信和Bid没有advId字段，暂时使用空字符串
        trackToken.setAdvId("");
        trackToken.setDspId(dsp.getDspId());
        trackToken.setTs(System.currentTimeMillis());
        // TODO 设置过期时间为2个小时（可根据不同广告类型调整）
        trackToken.setExpireAt(System.currentTimeMillis() + 2 * 60 * 60 * 1000L);
        trackToken.setUa(request.getDevice().getUa());
        trackToken.setIp(request.getDevice().getIp());
        trackToken.setIpv6(request.getDevice().getIpv6());
        trackToken.setPrice(bid.getPrice());
        trackToken.setOs(request.getDevice().getOs());
        trackToken.setOsv(request.getDevice().getOsv());

        Publisher publisher = metadataCacheService.getPublisher(publisherId);
        if (publisher != null) {
            trackToken.setRevShare(publisher.getRevShare());
        }
        // 默认使用CPM计费模式
        trackToken.setPriceMode(PriceMode.CPM.name());
        return trackToken;
    }
}
