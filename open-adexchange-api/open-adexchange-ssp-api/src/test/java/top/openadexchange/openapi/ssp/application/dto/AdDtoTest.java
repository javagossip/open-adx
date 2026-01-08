package top.openadexchange.openapi.ssp.application.dto;

import org.junit.jupiter.api.Test;

import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest.Device;
import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest.Geo;
import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest.Imp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AdDtoTest {

    @Test
    public void testAdFetchRequestDto() {
        AdFetchRequest request = new AdFetchRequest();
        request.setId("test-id");
        request.setTest(true);
        request.setDebug(false);

        // 创建Imp对象
        Imp imp = new Imp();
        imp.setId("imp-1");
        imp.setTagid("tag-123");
        request.setImp(Arrays.asList(imp));

        // 创建Device对象
        Geo geo = new Geo();
        geo.setLat(39.9042f);
        geo.setLon(116.4074f);

        Device device = new Device();
        device.setUa("Mozilla/5.0...");
        device.setGeo(geo);
        device.setIp("192.168.1.1");
        device.setDeviceType(1); // phone
        request.setDevice(device);

        // 测试字段设置
        assertEquals("test-id", request.getId());
        assertTrue(request.getTest());
        assertFalse(request.getDebug());
        assertEquals(1, request.getImp().size());
        assertEquals("imp-1", request.getImp().get(0).getId());
        assertEquals("tag-123", request.getImp().get(0).getTagid());
        assertEquals("192.168.1.1", request.getDevice().getIp());
        assertEquals(39.9042f, request.getDevice().getGeo().getLat());
    }

    @Test
    public void testAdFetchResponseDto() {
        AdFetchResponse response = new AdFetchResponse();
        response.setId("response-id");
        response.setBidid("bid-123");

        // 创建Bid对象
        AdFetchResponse.SeatBidDto.BidDto bid = new AdFetchResponse.SeatBidDto.BidDto();
        bid.setId("bid-1");
        bid.setImpid("imp-1");
        bid.setPrice(100.0f);
        bid.setCrid("creative-123");
        bid.setClickType(1); // 浏览器打开
        bid.setLdp("https://example.com");
        bid.setCreativeUrl("https://creative.example.com");

        // 创建SeatBid对象
        AdFetchResponse.SeatBidDto seatBid = new AdFetchResponse.SeatBidDto();
        seatBid.setBid(Arrays.asList(bid));
        seatBid.setSeat("seat-1");

        response.setSeatbid(Arrays.asList(seatBid));

        // 测试字段设置
        assertEquals("response-id", response.getId());
        assertEquals("bid-123", response.getBidid());
        assertEquals(1, response.getSeatbid().size());
        assertEquals("seat-1", response.getSeatbid().get(0).getSeat());
        assertEquals(1, response.getSeatbid().get(0).getBid().size());
        assertEquals("bid-1", response.getSeatbid().get(0).getBid().get(0).getId());
        assertEquals(100.0f, response.getSeatbid().get(0).getBid().get(0).getPrice());
    }

    @Test
    public void testNativeAdDto() {
        AdFetchResponse.NativeAdDto nativeAd = new AdFetchResponse.NativeAdDto();
        nativeAd.setTemplateId("template-1");
        
        Map<String, String> assets = new HashMap<>();
        assets.put("title", "广告标题");
        assets.put("description", "广告描述");
        nativeAd.setAssets(assets);

        AdFetchResponse.SeatBidDto.BidDto bid = new AdFetchResponse.SeatBidDto.BidDto();
        bid.setNativeAd(nativeAd);

        assertNotNull(bid.getNativeAd());
        assertEquals("template-1", bid.getNativeAd().getTemplateId());
        assertEquals("广告标题", bid.getNativeAd().getAssets().get("title"));
    }
}