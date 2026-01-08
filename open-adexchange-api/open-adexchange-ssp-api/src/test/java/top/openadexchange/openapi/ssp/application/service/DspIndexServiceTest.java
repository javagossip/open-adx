package top.openadexchange.openapi.ssp.application.service;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.roaringbitmap.RoaringBitmap;
import top.openadexchange.constants.Constants;
import top.openadexchange.constants.enums.DeviceType;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.application.dto.AdFetchRequest;
import top.openadexchange.openapi.ssp.domain.gateway.IP2RegionService;
import top.openadexchange.openapi.ssp.domain.model.IpLocation;
import top.openadexchange.openapi.ssp.repository.DspAggregateRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DspIndexServiceTest {

    @Mock
    private DspAggregateRepository dspAggregateRepository;

    @Mock
    private IP2RegionService ip2RegionService;

    @InjectMocks
    private DspIndexService dspIndexService;

    // 使用反射获取私有字段
    private Map<String, RoaringBitmap> adPlacementToDspIndex;
    private Map<String, RoaringBitmap> osIndex;
    private Map<String, RoaringBitmap> deviceTypeIndex;
    private Map<String, RoaringBitmap> regionIndex;

    @BeforeEach
    void setUp() throws Exception {
        // 使用反射获取私有字段
        Field adPlacementField = DspIndexService.class.getDeclaredField("adPlacementToDspIndex");
        adPlacementField.setAccessible(true);
        adPlacementToDspIndex = (Map<String, RoaringBitmap>) adPlacementField.get(dspIndexService);

        Field osField = DspIndexService.class.getDeclaredField("osIndex");
        osField.setAccessible(true);
        osIndex = (Map<String, RoaringBitmap>) osField.get(dspIndexService);

        Field deviceTypeField = DspIndexService.class.getDeclaredField("deviceTypeIndex");
        deviceTypeField.setAccessible(true);
        deviceTypeIndex = (Map<String, RoaringBitmap>) deviceTypeField.get(dspIndexService);

        Field regionField = DspIndexService.class.getDeclaredField("regionIndex");
        regionField.setAccessible(true);
        regionIndex = (Map<String, RoaringBitmap>) regionField.get(dspIndexService);
    }

    @Test
    void testInitDspIndex_Success() throws Exception {
        // 模拟数据
        Dsp dsp = new Dsp();
        dsp.setId(1);
        dsp.setName("Test DSP");

        DspTargeting targeting = new DspTargeting();
        targeting.setOs(JSON.toJSONString(Arrays.asList("IOS", "ANDROID")));
        targeting.setDeviceType(JSON.toJSONString(Arrays.asList("PHONE", "PAD")));
        targeting.setRegion(JSON.toJSONString(Arrays.asList("100000", "101000")));

        SiteAdPlacement siteAdPlacement = new SiteAdPlacement();
        siteAdPlacement.setId(1);
        siteAdPlacement.setCode("test_placement");

        DspAggregate dspAggregate = new DspAggregate(dsp, targeting, Collections.singletonList(1), Arrays.asList(siteAdPlacement));

        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Arrays.asList(dspAggregate), Collections.emptyList());

        // 调用方法
        dspIndexService.initDspIndex();

        // 验证结果
        assertFalse(adPlacementToDspIndex.isEmpty());
        assertFalse(osIndex.isEmpty());
        assertFalse(deviceTypeIndex.isEmpty());
        assertFalse(regionIndex.isEmpty());
    }

    @Test
    void testBuildDspIndex_EmptyResult() throws Exception {
        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Collections.emptyList());

        // 调用方法
        dspIndexService.initDspIndex();

        // 验证结果
        assertTrue(adPlacementToDspIndex.isEmpty());
        assertTrue(osIndex.isEmpty());
        assertTrue(deviceTypeIndex.isEmpty());
        assertTrue(regionIndex.isEmpty());
    }

    @Test
    void testMatchDspIds_WithMatchingConditions() throws Exception {
        // 准备测试数据
        Dsp dsp1 = new Dsp();
        dsp1.setId(1);
        dsp1.setName("Test DSP 1");

        Dsp dsp2 = new Dsp();
        dsp2.setId(2);
        dsp2.setName("Test DSP 2");

        DspTargeting targeting1 = new DspTargeting();
        targeting1.setOs(JSON.toJSONString(Arrays.asList("IOS")));
        targeting1.setDeviceType(JSON.toJSONString(Arrays.asList("PHONE")));
        targeting1.setRegion(JSON.toJSONString(Arrays.asList("100000")));

        DspTargeting targeting2 = new DspTargeting();
        targeting2.setOs(JSON.toJSONString(Arrays.asList("ANDROID")));
        targeting2.setDeviceType(JSON.toJSONString(Arrays.asList("PAD")));
        targeting2.setRegion(JSON.toJSONString(Arrays.asList("101000")));

        SiteAdPlacement siteAdPlacement1 = new SiteAdPlacement();
        siteAdPlacement1.setCode("test_placement_1");

        SiteAdPlacement siteAdPlacement2 = new SiteAdPlacement();
        siteAdPlacement2.setCode("test_placement_2");

        DspAggregate dspAggregate1 = new DspAggregate(dsp1, targeting1, Collections.singletonList(1), Arrays.asList(siteAdPlacement1));
        DspAggregate dspAggregate2 = new DspAggregate(dsp2, targeting2, Collections.singletonList(2), Arrays.asList(siteAdPlacement2));

        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Arrays.asList(dspAggregate1, dspAggregate2), Collections.emptyList());

        // 初始化索引
        dspIndexService.initDspIndex();

        // 创建请求
        AdFetchRequest request = new AdFetchRequest();
        AdFetchRequest.Device device = new AdFetchRequest.Device();
        device.setOs("IOS");
        device.setDeviceType(DeviceType.PHONE.ordinal()); // 使用 ordinal() 代替 getValue()
        device.setIp("192.168.1.1");
        request.setDevice(device);
        AdFetchRequest.Imp imp = new AdFetchRequest.Imp();
        imp.setTagid("test_placement_1");
        request.setImp(Arrays.asList(imp));

        // 模拟IP位置服务
        IpLocation ipLocation = new IpLocation("CN", "Beijing", "Beijing", "ISP", "100000");
        when(ip2RegionService.getRegion("192.168.1.1")).thenReturn(ipLocation);

        // 执行测试
        List<Integer> result = dspIndexService.matchDspIds(request);

        // 验证结果
        assertTrue(result.contains(1));
        assertFalse(result.contains(2));
    }

    @Test
    void testMatchDspIds_WithNoTargeting() throws Exception {
        // 准备测试数据
        Dsp dsp = new Dsp();
        dsp.setId(1);
        dsp.setName("Test DSP No Targeting");

        DspTargeting targeting = null; // 无定向信息

        SiteAdPlacement siteAdPlacement = new SiteAdPlacement();
        siteAdPlacement.setCode("test_placement");

        DspAggregate dspAggregate = new DspAggregate(dsp, targeting, Collections.singletonList(1), Arrays.asList(siteAdPlacement));

        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Arrays.asList(dspAggregate), Collections.emptyList());

        // 初始化索引
        dspIndexService.initDspIndex();

        // 创建请求
        AdFetchRequest request = new AdFetchRequest();
        AdFetchRequest.Device device = new AdFetchRequest.Device();
        device.setOs("IOS");
        device.setDeviceType(DeviceType.PHONE.ordinal()); // 使用 ordinal() 代替 getValue()
        device.setIp("192.168.1.1");
        request.setDevice(device);
        AdFetchRequest.Imp imp = new AdFetchRequest.Imp();
        imp.setTagid("test_placement");
        request.setImp(Arrays.asList(imp));

        // 模拟IP位置服务
        IpLocation ipLocation = new IpLocation("CN", "Beijing", "Beijing", "ISP", "100000");
        when(ip2RegionService.getRegion("192.168.1.1")).thenReturn(ipLocation);

        // 执行测试
        List<Integer> result = dspIndexService.matchDspIds(request);

        // 验证结果 - 应该包含这个DSP，因为没有定向限制
        assertTrue(result.contains(1));
    }

    @Test
    void testMatchDspIds_WithDefaultTargeting() throws Exception {
        // 准备测试数据
        Dsp dsp = new Dsp();
        dsp.setId(1);
        dsp.setName("Test DSP Default Targeting");

        DspTargeting targeting = new DspTargeting();
        // 使用默认定向（不限制特定值）
        targeting.setOs(null);
        targeting.setDeviceType(null);
        targeting.setRegion(null);

        SiteAdPlacement siteAdPlacement = new SiteAdPlacement();
        siteAdPlacement.setCode("test_placement");

        DspAggregate dspAggregate = new DspAggregate(dsp, targeting, Collections.singletonList(1), Arrays.asList(siteAdPlacement));

        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Arrays.asList(dspAggregate), Collections.emptyList());

        // 初始化索引
        dspIndexService.initDspIndex();

        // 创建请求
        AdFetchRequest request = new AdFetchRequest();
        AdFetchRequest.Device device = new AdFetchRequest.Device();
        device.setOs("ANDROID");
        device.setDeviceType(DeviceType.PAD.ordinal()); // 使用 ordinal() 代替 getValue()
        device.setIp("192.168.1.2");
        request.setDevice(device);
        AdFetchRequest.Imp imp = new AdFetchRequest.Imp();
        imp.setTagid("test_placement");
        request.setImp(Arrays.asList(imp));

        // 模拟IP位置服务
        IpLocation ipLocation = new IpLocation("CN", "Shanghai", "Shanghai", "ISP", "101000");
        when(ip2RegionService.getRegion("192.168.1.2")).thenReturn(ipLocation);

        // 执行测试
        List<Integer> result = dspIndexService.matchDspIds(request);

        // 验证结果 - 应该包含这个DSP，因为使用了默认定向
        assertTrue(result.contains(1));
    }

    @Test
    void testMatchDspIds_WithNoMatchingConditions() throws Exception {
        // 准备测试数据
        Dsp dsp = new Dsp();
        dsp.setId(1);
        dsp.setName("Test DSP");

        DspTargeting targeting = new DspTargeting();
        targeting.setOs(JSON.toJSONString(Arrays.asList("ANDROID")));
        targeting.setDeviceType(JSON.toJSONString(Arrays.asList("PAD")));
        targeting.setRegion(JSON.toJSONString(Arrays.asList("101000")));

        SiteAdPlacement siteAdPlacement = new SiteAdPlacement();
        siteAdPlacement.setCode("test_placement");

        DspAggregate dspAggregate = new DspAggregate(dsp, targeting, Collections.singletonList(1), Arrays.asList(siteAdPlacement));

        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Arrays.asList(dspAggregate), Collections.emptyList());

        // 初始化索引
        dspIndexService.initDspIndex();

        // 创建请求 - 与定向条件不匹配
        AdFetchRequest request = new AdFetchRequest();
        AdFetchRequest.Device device = new AdFetchRequest.Device();
        device.setOs("IOS");
        device.setDeviceType(DeviceType.PHONE.ordinal()); // 使用 ordinal() 代替 getValue()
        device.setIp("192.168.1.1");
        request.setDevice(device);
        AdFetchRequest.Imp imp = new AdFetchRequest.Imp();
        imp.setTagid("test_placement");
        request.setImp(Arrays.asList(imp));

        // 模拟IP位置服务 - 与定向区域不匹配
        IpLocation ipLocation = new IpLocation("CN", "Beijing", "Beijing", "ISP", "200000");
        when(ip2RegionService.getRegion("192.168.1.1")).thenReturn(ipLocation);

        // 执行测试
        List<Integer> result = dspIndexService.matchDspIds(request);

        // 验证结果 - 应该不包含任何DSP，因为条件不匹配
        assertTrue(result.isEmpty());
    }

    @Test
    void testMatchDspIds_WithTagIds() throws Exception {
        // 准备测试数据
        Dsp dsp = new Dsp();
        dsp.setId(1);
        dsp.setName("Test DSP");

        DspTargeting targeting = new DspTargeting();
        targeting.setOs(null);
        targeting.setDeviceType(null);
        targeting.setRegion(null);

        SiteAdPlacement siteAdPlacement = new SiteAdPlacement();
        siteAdPlacement.setCode("test_tag_id");

        DspAggregate dspAggregate = new DspAggregate(dsp, targeting, Collections.singletonList(1), Arrays.asList(siteAdPlacement));

        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Arrays.asList(dspAggregate), Collections.emptyList());

        // 初始化索引
        dspIndexService.initDspIndex();

        // 创建请求
        AdFetchRequest request = new AdFetchRequest();
        AdFetchRequest.Device device = new AdFetchRequest.Device();
        device.setOs("IOS");
        device.setDeviceType(DeviceType.PHONE.ordinal()); // 使用 ordinal() 代替 getValue()
        device.setIp("192.168.1.1");
        request.setDevice(device);

        // 添加Imp，包含tagid
        AdFetchRequest.Imp imp = new AdFetchRequest.Imp();
        imp.setTagid("test_tag_id");
        request.setImp(Arrays.asList(imp));

        // 模拟IP位置服务
        IpLocation ipLocation = new IpLocation("CN", "Beijing", "Beijing", "ISP", "100000");
        when(ip2RegionService.getRegion("192.168.1.1")).thenReturn(ipLocation);

        // 执行测试
        List<Integer> result = dspIndexService.matchDspIds(request);

        // 验证结果 - 应该包含这个DSP，因为tagid匹配
        assertTrue(result.contains(1));
    }

    @Test
    void testMatchDspIds_WithNoIpLocation() throws Exception {
        // 准备测试数据
        Dsp dsp = new Dsp();
        dsp.setId(1);
        dsp.setName("Test DSP");

        DspTargeting targeting = new DspTargeting();
        targeting.setOs(null);
        targeting.setDeviceType(null);
        targeting.setRegion(null);

        SiteAdPlacement siteAdPlacement = new SiteAdPlacement();
        siteAdPlacement.setCode(Constants.DEFAULT_ALL_TARGETING);

        DspAggregate dspAggregate = new DspAggregate(dsp, targeting, Collections.singletonList(1), Arrays.asList(siteAdPlacement));

        when(dspAggregateRepository.listDspsByPageNo(anyInt())).thenReturn(Arrays.asList(dspAggregate), Collections.emptyList());

        // 初始化索引
        dspIndexService.initDspIndex();

        // 创建请求
        AdFetchRequest request = new AdFetchRequest();
        AdFetchRequest.Device device = new AdFetchRequest.Device();
        device.setOs("IOS");
        device.setDeviceType(DeviceType.PHONE.ordinal()); // 使用 ordinal() 代替 getValue()
        device.setIp("invalid_ip");
        request.setDevice(device);
        AdFetchRequest.Imp imp = new AdFetchRequest.Imp();
        imp.setTagid(Constants.DEFAULT_ALL_TARGETING);
        request.setImp(Arrays.asList(imp));

        // 模拟IP位置服务返回null
        when(ip2RegionService.getRegion("invalid_ip")).thenReturn(null);

        // 执行测试
        List<Integer> result = dspIndexService.matchDspIds(request);

        // 验证结果 - 应该包含这个DSP，因为使用了默认定向且没有IP位置信息
        assertTrue(result.contains(1));
    }
}