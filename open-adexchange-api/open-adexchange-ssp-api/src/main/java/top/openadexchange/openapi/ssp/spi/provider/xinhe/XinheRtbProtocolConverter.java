package top.openadexchange.openapi.ssp.spi.provider.xinhe;

import com.alibaba.fastjson2.JSON;
import com.chaincoretech.epc.annotation.Extension;

import top.openadexchange.model.Dsp;
import top.openadexchange.openapi.ssp.spi.RtbProtocolConverter;
import top.openadexchange.rtb.proto.OaxRtbProto;
import top.openadexchange.rtb.proto.OaxRtbProto.BidResponse.NativeAd;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.App;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.Bid;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.BidRequest;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.BidResponse;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.ConnectionType;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.Device;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.DeviceType;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.Gender;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.Geo;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.Imp;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.OperatorType;
import top.openadexchange.rtb.proto.provider.xinhe.XinHeRtbProto.User;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Extension(keys = {"xinhe"})
public class XinheRtbProtocolConverter implements RtbProtocolConverter<BidRequest, BidResponse> {

    // OAX连接类型 -> 信和连接类型映射
    public static final Map<Integer, ConnectionType> CONNECTION_TYPE_MAP = new HashMap<>();

    // OAX运营商 -> 信和运营商映射
    public static final Map<Integer, OperatorType> OPERATOR_TYPE_MAP = new HashMap<>();

    // OAX设备类型 -> 信和设备类型映射
    public static final Map<Integer, DeviceType> DEVICE_TYPE_MAP = new HashMap<>();
    private static final /**<tagId,impId>**/
            Map<String, String> TAGID_IMP_MAP = new HashMap<>();

    static {
        // 连接类型映射: OAX (0-未知, 1-wifi, 2-2G, 3-3G, 4-4G, 5-5G) -> 信和
        CONNECTION_TYPE_MAP.put(0, ConnectionType.CT_UNKNOWN);      // 未知 -> 未知
        CONNECTION_TYPE_MAP.put(1, ConnectionType.CT_WIFI);          // wifi -> wifi
        CONNECTION_TYPE_MAP.put(2, ConnectionType.CT_CELLULAR_2G);  // 2G -> 2G
        CONNECTION_TYPE_MAP.put(3, ConnectionType.CT_CELLULAR_3G);  // 3G -> 3G
        CONNECTION_TYPE_MAP.put(4, ConnectionType.CT_CELLULAR_4G);  // 4G -> 4G
        CONNECTION_TYPE_MAP.put(5, ConnectionType.CT_CELLULAR_5G);  // 5G -> 5G

        // 运营商映射: OAX (0-未知, 1-移动, 2-联通, 3-电信) -> 信和
        OPERATOR_TYPE_MAP.put(0, OperatorType.OT_UNKNOWN);  // 未知 -> 未知
        OPERATOR_TYPE_MAP.put(1, OperatorType.OT_MOBILE);   // 移动 -> 移动
        OPERATOR_TYPE_MAP.put(2, OperatorType.OT_UNICOM);    // 联通 -> 联通
        OPERATOR_TYPE_MAP.put(3, OperatorType.OT_TELECOM);  // 电信 -> 电信

        // 设备类型映射: OAX (1-phone, 2-pad, 3-pc, 4-tv) -> 信和
        DEVICE_TYPE_MAP.put(1, DeviceType.DT_PHONE);  // phone -> PHONE
        DEVICE_TYPE_MAP.put(2, DeviceType.DT_PAD);    // pad -> PAD
        DEVICE_TYPE_MAP.put(3, DeviceType.DT_PC);     // pc -> PC
        DEVICE_TYPE_MAP.put(4, DeviceType.DT_TV);     // tv -> TV
    }

    @Override
    public BidRequest to(Dsp dsp, OaxRtbProto.BidRequest bidRequest) {
        BidRequest.Builder builder = BidRequest.newBuilder();
        builder.setId(bidRequest.getId());
        builder.setTmax(dsp.getTimeoutMs());
        builder.setTest(bidRequest.getTest());

        builder.setApp(buildApp(dsp, bidRequest));
        builder.setDevice(buildDevice(dsp, bidRequest));
        bidRequest.getImpList().forEach(imp -> builder.addImp(buildImp(dsp, imp)));
        builder.setUser(User.newBuilder().setGender(Gender.UNRECOGNIZED).build());

        return builder.build();
    }

    private Device buildDevice(Dsp dsp, OaxRtbProto.BidRequest bidRequest) {
        Device.Builder builder = Device.newBuilder();
        builder.setConnectiontype(CONNECTION_TYPE_MAP.getOrDefault(bidRequest.getDevice().getConnectionType(),
                ConnectionType.CT_UNKNOWN));
        builder.setIp(bidRequest.getDevice().getIp());
        builder.setOs(bidRequest.getDevice().getOs());
        builder.setOsv(bidRequest.getDevice().getOsv());
        builder.setDevicetype(DEVICE_TYPE_MAP.getOrDefault(bidRequest.getDevice().getDeviceType(),
                DeviceType.DT_UNKNOWN));
        builder.setMake(bidRequest.getDevice().getMake());
        builder.setModel(bidRequest.getDevice().getModel());
        builder.setUa(bidRequest.getDevice().getUa());
        builder.setH(bidRequest.getDevice().getH());
        builder.setW(bidRequest.getDevice().getW());
        builder.setOperatortype(OPERATOR_TYPE_MAP.getOrDefault(bidRequest.getDevice().getCarrier(),
                OperatorType.OT_UNKNOWN));
        builder.setDid(bidRequest.getDevice().getIfa());
        builder.setDidmd5(bidRequest.getDevice().getDidmd5());
        builder.setIpv6(bidRequest.getDevice().getIpv6());
        builder.setMac(bidRequest.getDevice().getMac());
        builder.setGeo(Geo.newBuilder()
                .setLat(bidRequest.getDevice().getGeo().getLat())
                .setLon(bidRequest.getDevice().getGeo().getLon())
                .build());
        return builder.build();
    }

    private App buildApp(Dsp dsp, OaxRtbProto.BidRequest bidRequest) {
        App.Builder builder = App.newBuilder();
        builder.setBundle(bidRequest.getApp().getBundle());
        builder.setName(bidRequest.getApp().getName());
        builder.setVer(bidRequest.getApp().getVer());
        return builder.build();
    }

    private Imp buildImp(Dsp dsp, OaxRtbProto.BidRequest.Imp imp) {
        Imp.Builder builder = Imp.newBuilder();
        builder.setSlotId(imp.getTagid());
        TAGID_IMP_MAP.put(imp.getTagid(), imp.getId());
        builder.setBidfloor((int) imp.getBidFloor());
        imp.getPmp().getDealsList().forEach(deal -> builder.addDealIds(deal.getId()));

        if (imp.hasBanner()) {
            builder.setWidth(imp.getBanner().getW());
            builder.setHeight(imp.getBanner().getH());
        } else if (imp.hasVideo()) {
            builder.setWidth(imp.getVideo().getW());
            builder.setHeight(imp.getVideo().getH());
        }
        return builder.build();
    }

    @Override
    public OaxRtbProto.BidResponse from(Dsp dsp, BidResponse bidResponse) {
        OaxRtbProto.BidResponse.Builder builder = OaxRtbProto.BidResponse.newBuilder();
        builder.setId(bidResponse.getId());
        builder.setBidid(bidResponse.getBidid());

        // 处理SeatBid
        for (XinHeRtbProto.SeatBid xinheSeatBid : bidResponse.getSeatBidList()) {
            OaxRtbProto.BidResponse.SeatBid.Builder seatBidBuilder = OaxRtbProto.BidResponse.SeatBid.newBuilder();
            //seatBidBuilder.
            xinheSeatBid.getBidList().forEach(xinheBid -> seatBidBuilder.addBid(buildOaxBid(xinheBid)));
            // 处理Bid
            // 注意：信和协议的Bid没有impid字段，这意味着无法准确关联到具体的Imp
            // 在实际使用中，可能需要通过其他方式（如上下文）来确定Imp的关联
            for (XinHeRtbProto.Bid xinheBid : xinheSeatBid.getBidList()) {
                seatBidBuilder.addBid(buildOaxBid(xinheBid));
            }
            builder.addSeatbid(seatBidBuilder.build());
        }
        return builder.build();
    }

    /**
     * 将信和的Bid转换为OAX的Bid 注意：信和协议的Bid没有impid字段，这里生成的OaxBid也不会有impid
     */
    private OaxRtbProto.BidResponse.SeatBid.Bid buildOaxBid(XinHeRtbProto.Bid xinheBid) {
        OaxRtbProto.BidResponse.SeatBid.Bid.Builder builder = OaxRtbProto.BidResponse.SeatBid.Bid.newBuilder();
        builder.setNurl(xinheBid.getNurl());
        builder.setPrice(xinheBid.getPrice());
        builder.setId(xinheBid.getId());
        builder.setBundle(xinheBid.getDpkgname());
        builder.setAppName(xinheBid.getAppInfo().getAppName());
        builder.setAppDownloadUrl(xinheBid.getDurl());
        builder.setClickType(xinheBid.getCtype());
        builder.setLdp(xinheBid.getCurl());

        //图片广告获取第一张图片
        builder.setCreativeUrl(xinheBid.getAurlList().isEmpty() ? "" : xinheBid.getAurlList().get(0));
        if (xinheBid.hasVideo()) {
            builder.setCreativeUrl(xinheBid.getVideo().getVideourl());
        }
        builder.addAllImpTrackers(xinheBid.getMurlList());
        builder.addAllClkTrackers(xinheBid.getCmurlList());
        builder.setDeeplink(xinheBid.getDeeplinkurl());

        NativeAd.Builder nativeAd = NativeAd.newBuilder();
        nativeAd.setTemplateId(String.valueOf(xinheBid.getTemplateid()));
        nativeAd.putAllAssets(buildNativeAssets(xinheBid));
        builder.setNativeAd(nativeAd.build());
        return builder.build();
    }

    private Map<String, String> buildNativeAssets(Bid xinheBid) {
        Map<String, String> assets = new HashMap<>();
        xinheBid.getAurlList().forEach(url -> assets.put("img", url));
        assets.put("title", xinheBid.getTitle());
        assets.put("desc", xinheBid.getSummary());
        assets.put("images", JSON.toJSONString(xinheBid.getAurlList()));
        assets.put("icon", xinheBid.getIcon());
        return assets;
    }
}
