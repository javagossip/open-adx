package top.openadexchange.openapi.ssp.domain.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.application.factory.IndexKeysBuilder;
import top.openadexchange.openapi.ssp.config.OaxEngineProperties;
import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactories;
import top.openadexchange.openapi.ssp.domain.gateway.ExecutorFactory;
import top.openadexchange.openapi.ssp.domain.gateway.IndexService;
import top.openadexchange.openapi.ssp.domain.gateway.MetadataRepository;
import top.openadexchange.openapi.ssp.domain.gateway.OaxEngineServices;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;
import top.openadexchange.openapi.ssp.spi.MacroContextBuilder;
import top.openadexchange.openapi.ssp.spi.MacroProcessor;
import top.openadexchange.openapi.ssp.spi.factory.OaxSpiFactory;
import top.openadexchange.openapi.ssp.spi.model.MacroContext;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;
import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest.Imp;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.SeatBid.Bid.Builder;

@Service
@Slf4j
public class AdExchangeEngine {

    private static final long DELTA = 10000; //1分

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

    public Map<String, Bid> bidding(BidRequest request) {
        // 1. 获取所有 DSP 的响应 (并发逻辑同前)
        Map<String, Long> impFloorMap =
                request.getImpList().stream().collect(Collectors.toMap(Imp::getId, Imp::getBidFloor, (a, b) -> a));
        Map<String, List<DspBid>> validImpBids = fetchAllBids(request, impFloorMap);
        if (validImpBids == null || validImpBids.isEmpty()) {
            return null;
        }
        Map<String, Bid> winnerBids = new HashMap<>();
        validImpBids.forEach((impId, bids) -> winnerBids.put(impId, selectWinBid(bids, impFloorMap.get(impId))));
        return winnerBids;
    }

    private Bid selectWinBid(List<DspBid> bids, long impFloor) {
        // 3. 执行二价计费算法
        List<DspBid> sortedBids = bids.stream()
                .sorted(Comparator.comparingDouble(DspBid::getPrice).reversed())
                .collect(Collectors.toList());
        DspBid winner = sortedBids.get(0);
        long settlementPrice;

        if (sortedBids.size() > 1) {
            // 有多个竞标者，取第二名价格 + DELTA
            long secondPrice = sortedBids.get(1).getBid().getPrice();
            settlementPrice = secondPrice + DELTA;

            // 兜底：结算价不能超过中标者自己的出价
            settlementPrice = Math.min(settlementPrice, winner.getBid().getPrice());
        } else {
            // 只有一个竞标者，按底价结算
            settlementPrice = impFloor;
        }

        // 4. 设置最终结算价格并返回
        Dsp winDsp = winner.getDsp();
        Bid.Builder builder = Bid.newBuilder(winner.getBid()).setPrice(settlementPrice);
        //5. 对WinNotice url以及点击/曝光监测地址进行宏替换处理
        replaceMacros(builder, winner);
        //6. 发送WinNotice请求
        log.info("竞价完成，中标者: {}, 原始出价: {}, 最终结算价: {}",
                winDsp.getName(),
                winner.getBid().getPrice(),
                settlementPrice);

        return builder.build();
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
    private Map<String, List<DspBid>> fetchAllBids(BidRequest request, /*<impid, floorPrice>*/
            Map<String, Long> impFloorMap) {
        IndexService indexService = oaxEngineServices.getIndexService();
        MetadataRepository metadataRepository = oaxEngineServices.getCachedMetadataRepository();

        IndexKeys indexKeys = indexKeysBuilder.buildIndexKeys(request);
        //从索引库中查询匹配当前广告流量的dsp列表
        List<Integer> matchDspIds = indexService.searchDsps(indexKeys);

        if (matchDspIds.isEmpty()) {
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
                List<SeatBid> seatBids = dspBidResponse.getSeatbidList();
                for (SeatBid seatBid : seatBids) {
                    for (Bid bid : seatBid.getBidList()) {
                        Long floor = impFloorMap.get(bid.getImpid());
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
        private Bid bid;

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
        private BidResponse bidResponse;

        private DspBidResponse(Dsp dsp, BidResponse bidResponse) {
            this.dsp = dsp;
            this.bidResponse = bidResponse;
        }

        public static DspBidResponse of(Dsp dsp, BidResponse bidResponse) {
            return new DspBidResponse(dsp, bidResponse);
        }

        public List<SeatBid> getSeatbidList() {
            return bidResponse.getSeatbidList();
        }
    }
}
