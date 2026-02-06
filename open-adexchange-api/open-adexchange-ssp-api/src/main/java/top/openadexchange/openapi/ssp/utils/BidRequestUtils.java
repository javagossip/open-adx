package top.openadexchange.openapi.ssp.utils;

import top.openadexchange.rtb.proto.OaxRtbProto.BidRequest;

public class BidRequestUtils {

    public static boolean traceEnabled(BidRequest.Builder request) {
        return request.getTest() || request.getDebug();
    }

    public static boolean traceEnabled(BidRequest request) {
        return request.getTest() || request.getDebug();
    }
}
