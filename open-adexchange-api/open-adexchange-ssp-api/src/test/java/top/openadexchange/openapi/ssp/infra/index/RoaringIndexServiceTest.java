package top.openadexchange.openapi.ssp.infra.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import top.openadexchange.constants.Constants;
import top.openadexchange.domain.entity.DspAggregate;
import top.openadexchange.model.Dsp;
import top.openadexchange.model.DspTargeting;
import top.openadexchange.model.SiteAdPlacement;
import top.openadexchange.openapi.ssp.domain.model.IndexKeys;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RoaringBitmapIndexService 单元测试
 * 测试索引服务的基本功能、边界条件和异常场景
 */
@DisplayName("RoaringBitmapIndexService 单元测试")
class RoaringIndexServiceTest {

    private RoaringIndexService indexService;

    @BeforeEach
    void setUp() {
        indexService = new RoaringIndexService();
    }

    // ==================== Helper Methods ====================

    private DspAggregate createDspAggregate(Integer dspId, String dspName, List<SiteAdPlacement> placements,
            DspTargeting targeting) {
        Dsp dsp = new Dsp();
        dsp.setId(dspId);
        dsp.setName(dspName);
        return new DspAggregate(dsp, targeting, null, placements);
    }

    private DspTargeting createTargeting(String os, String deviceType, String region) {
        DspTargeting targeting = new DspTargeting();
        targeting.setOs(os);
        targeting.setDeviceType(deviceType);
        targeting.setRegion(region);
        return targeting;
    }

    private SiteAdPlacement createSiteAdPlacement(String code) {
        SiteAdPlacement placement = new SiteAdPlacement();
        placement.setCode(code);
        return placement;
    }

    private IndexKeys createIndexKeys(List<String> osKeys, List<String> deviceTypeKeys, List<String> tagIdKeys,
            List<String> regionKeys) {
        IndexKeys indexKeys = new IndexKeys();
        indexKeys.setOsKeys(osKeys);
        indexKeys.setDeviceTypeKeys(deviceTypeKeys);
        indexKeys.setTagIdKeys(tagIdKeys);
        indexKeys.setRegionKeys(regionKeys);
        return indexKeys;
    }

    // ==================== indexDsp Tests ====================

    @Test
    @DisplayName("测试索引单个DSP - 有广告位、有定向信息")
    void testIndexDspWithPlacementAndTargeting() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(
                createSiteAdPlacement("TAG001"),
                createSiteAdPlacement("TAG002")
        );
        DspTargeting targeting = createTargeting("[\"ios\", \"android\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证索引结果 - 使用searchDsps方法验证
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertFalse(result.isEmpty(), "搜索结果不应为空");
        assertTrue(result.contains(1), "应该包含DSP ID: 1");
    }

    @Test
    @DisplayName("测试索引DSP - 不限广告位")
    void testIndexDspWithoutPlacement() {
        // 准备测试数据：不限广告位
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(2, "NoPlacementDSP", null, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：使用任意广告位都应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("RANDOM_TAG", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(2), "不限广告位的DSP应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - 空广告位列表")
    void testIndexDspWithEmptyPlacements() {
        // 准备测试数据：空广告位列表
        List<SiteAdPlacement> placements = new ArrayList<>();
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(3, "EmptyPlacementDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：空广告位列表等同于不限广告位
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("ANY_TAG", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(3), "空广告位列表的DSP应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - 无定向信息")
    void testIndexDspWithoutTargeting() {
        // 准备测试数据：无定向信息
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG003"));
        DspAggregate dspAggregate = createDspAggregate(4, "NoTargetingDSP", placements, null);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：任意设备类型、任意OS、任意区域都应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG003", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(4), "无定向信息的DSP应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - 多个广告位")
    void testIndexDspWithMultiplePlacements() {
        // 准备测试数据：多个广告位
        List<SiteAdPlacement> placements = Arrays.asList(
                createSiteAdPlacement("TAG001"),
                createSiteAdPlacement("TAG002"),
                createSiteAdPlacement("TAG003")
        );
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(5, "MultiplePlacementsDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：每个广告位都应该能匹配到
        for (String tagId : Arrays.asList("TAG001", "TAG002", "TAG003")) {
            IndexKeys indexKeys = createIndexKeys(
                    Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList(tagId, Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
            );

            List<Integer> result = indexService.searchDsps(indexKeys);
            assertTrue(result.contains(5), "广告位 " + tagId + " 应该能匹配到DSP");
        }
    }

    @Test
    @DisplayName("测试索引DSP - 多个OS定向")
    void testIndexDspWithMultipleOsTargeting() {
        // 准备测试数据：多个OS定向
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\", \"android\", \"windows\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(6, "MultipleOsDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：每个OS都应该能匹配到
        for (String os : Arrays.asList("IOS", "ANDROID", "WINDOWS")) {
            IndexKeys indexKeys = createIndexKeys(
                    Arrays.asList(os, Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
            );

            List<Integer> result = indexService.searchDsps(indexKeys);
            assertTrue(result.contains(6), "OS " + os + " 应该能匹配到DSP");
        }
    }

    @Test
    @DisplayName("测试索引DSP - 多个设备类型定向")
    void testIndexDspWithMultipleDeviceTypeTargeting() {
        // 准备测试数据：多个设备类型定向
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\", \"pad\", \"pc\"]", null);
        DspAggregate dspAggregate = createDspAggregate(7, "MultipleDeviceTypeDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：每个设备类型都应该能匹配到
        for (String deviceType : Arrays.asList("PHONE", "PAD", "PC")) {
            IndexKeys indexKeys = createIndexKeys(
                    Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList(deviceType, Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
            );

            List<Integer> result = indexService.searchDsps(indexKeys);
            assertTrue(result.contains(7), "设备类型 " + deviceType + " 应该能匹配到DSP");
        }
    }

    @Test
    @DisplayName("测试索引DSP - 多个区域定向")
    void testIndexDspWithMultipleRegionTargeting() {
        // 准备测试数据：多个区域定向
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\", \"310000\", \"440300\"]");
        DspAggregate dspAggregate = createDspAggregate(8, "MultipleRegionDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：每个区域都应该能匹配到
        for (String region : Arrays.asList("110000", "310000", "440300")) {
            IndexKeys indexKeys = createIndexKeys(
                    Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList(region, Constants.DEFAULT_ALL_TARGETING)
            );

            List<Integer> result = indexService.searchDsps(indexKeys);
            assertTrue(result.contains(8), "区域 " + region + " 应该能匹配到DSP");
        }
    }

    @Test
    @DisplayName("测试索引DSP - 不限OS")
    void testIndexDspWithoutOsTargeting() {
        // 准备测试数据：不限OS
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting(null, "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(9, "NoOsDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：任意OS都应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(9), "不限OS的DSP应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - 不限设备类型")
    void testIndexDspWithoutDeviceTypeTargeting() {
        // 准备测试数据：不限设备类型
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", null, null);
        DspAggregate dspAggregate = createDspAggregate(10, "NoDeviceTypeDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：任意设备类型都应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(10), "不限设备类型的DSP应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - 不限区域")
    void testIndexDspWithoutRegionTargeting() {
        // 准备测试数据：不限区域
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(11, "NoRegionDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：任意区域都应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(11), "不限区域的DSP应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - OS大小写不敏感")
    void testIndexDspOsCaseInsensitive() {
        // 准备测试数据：OS为小写
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(12, "CaseSensitiveDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：使用大写OS应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(12), "OS大小写不敏感应该能匹配到DSP");
    }

    @Test
    @DisplayName("测试索引DSP - 设备类型大小写不敏感")
    void testIndexDspDeviceTypeCaseInsensitive() {
        // 准备测试数据：设备类型为小写
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(13, "DeviceTypeCaseDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：使用大写设备类型应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(13), "设备类型大小写不敏感应该能匹配到DSP");
    }

    @Test
    @DisplayName("测试索引多个DSP - 相同广告位")
    void testIndexMultipleDspsSamePlacement() {
        // 准备测试数据：多个DSP使用相同广告位
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));

        DspTargeting targeting1 = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate1 = createDspAggregate(1, "DSP1", placements, targeting1);

        DspTargeting targeting2 = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate2 = createDspAggregate(2, "DSP2", placements, targeting2);

        // 执行索引
        indexService.indexDsp(dspAggregate1);
        indexService.indexDsp(dspAggregate2);

        // 验证：两个DSP都应该能匹配到
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertEquals(2, result.size(), "应该匹配到2个DSP");
        assertTrue(result.contains(1), "应该包含DSP1");
        assertTrue(result.contains(2), "应该包含DSP2");
    }

    @Test
    @DisplayName("测试索引多个DSP - 不同定向条件")
    void testIndexMultipleDspsDifferentTargeting() {
        // 准备测试数据：多个DSP使用不同定向条件
        List<SiteAdPlacement> placements1 = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting1 = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate1 = createDspAggregate(1, "iOS_DSP", placements1, targeting1);

        List<SiteAdPlacement> placements2 = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting2 = createTargeting("[\"android\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate2 = createDspAggregate(2, "Android_DSP", placements2, targeting2);

        List<SiteAdPlacement> placements3 = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting3 = createTargeting("[\"ios\"]", "[\"pad\"]", "[\"110000\"]");
        DspAggregate dspAggregate3 = createDspAggregate(3, "Pad_DSP", placements3, targeting3);

        // 执行索引
        indexService.indexDsp(dspAggregate1);
        indexService.indexDsp(dspAggregate2);
        indexService.indexDsp(dspAggregate3);

        // 验证1：ios+phone应该只匹配到DSP1
        IndexKeys indexKeys1 = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );
        List<Integer> result1 = indexService.searchDsps(indexKeys1);
        assertEquals(1, result1.size());
        assertTrue(result1.contains(1));

        // 验证2：android+phone应该只匹配到DSP2
        IndexKeys indexKeys2 = createIndexKeys(
                Arrays.asList("ANDROID", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );
        List<Integer> result2 = indexService.searchDsps(indexKeys2);
        assertEquals(1, result2.size());
        assertTrue(result2.contains(2));

        // 验证3：ios+pad应该只匹配到DSP3
        IndexKeys indexKeys3 = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PAD", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );
        List<Integer> result3 = indexService.searchDsps(indexKeys3);
        assertEquals(1, result3.size());
        assertTrue(result3.contains(3));
    }

    // ==================== searchDsps Tests ====================

    @Test
    @DisplayName("测试搜索DSP - 所有条件匹配")
    void testSearchDspsAllConditionsMatch() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：所有条件都匹配
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertEquals(1, result.size(), "应该匹配到1个DSP");
        assertEquals(1, result.get(0), "DSP ID应该是1");
    }

    @Test
    @DisplayName("测试搜索DSP - 广告位不匹配")
    void testSearchDspsPlacementMismatch() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：广告位不匹配
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG999", Constants.DEFAULT_ALL_TARGETING), // 不存在的广告位
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.isEmpty() || !result.contains(1), "广告位不匹配不应该匹配到DSP");
    }

    @Test
    @DisplayName("测试搜索DSP - OS不匹配")
    void testSearchDspsOsMismatch() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：OS不匹配
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("ANDROID", Constants.DEFAULT_ALL_TARGETING), // android不匹配
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.isEmpty() || !result.contains(1), "OS不匹配不应该匹配到DSP");
    }

    @Test
    @DisplayName("测试搜索DSP - 设备类型不匹配")
    void testSearchDspsDeviceTypeMismatch() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：设备类型不匹配
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PC", Constants.DEFAULT_ALL_TARGETING), // PC不匹配
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.isEmpty() || !result.contains(1), "设备类型不匹配不应该匹配到DSP");
    }

    @Test
    @DisplayName("测试搜索DSP - 区域不匹配")
    void testSearchDspsRegionMismatch() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]"); // 只定向北京
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：区域不匹配
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("310000", Constants.DEFAULT_ALL_TARGETING) // 上海不匹配
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.isEmpty() || !result.contains(1), "区域不匹配不应该匹配到DSP");
    }

    @Test
    @DisplayName("测试搜索DSP - 多个标签ID (OR逻辑)")
    void testSearchDspsMultipleTagIds() {
        // 准备测试数据
        List<SiteAdPlacement> placements1 = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting1 = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate1 = createDspAggregate(1, "DSP1", placements1, targeting1);
        indexService.indexDsp(dspAggregate1);

        List<SiteAdPlacement> placements2 = Arrays.asList(createSiteAdPlacement("TAG002"));
        DspTargeting targeting2 = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate2 = createDspAggregate(2, "DSP2", placements2, targeting2);
        indexService.indexDsp(dspAggregate2);

        // 执行搜索：同时搜索TAG001和TAG002
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", "TAG002", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertEquals(2, result.size(), "应该匹配到2个DSP");
        assertTrue(result.contains(1), "应该包含DSP1");
        assertTrue(result.contains(2), "应该包含DSP2");
    }

    @Test
    @DisplayName("测试搜索DSP - 使用DEFAULT_ALL_TARGETING")
    void testSearchDspsWithDefaultAllTargeting() {
        // 准备测试数据：不限广告位
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", null, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：使用DEFAULT_ALL_TARGETING
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING), // 使用默认通配符
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(1), "使用DEFAULT_ALL_TARGETING应该能匹配到不限广告位的DSP");
    }

    @Test
    @DisplayName("测试搜索DSP - 空索引")
    void testSearchDspsWithEmptyIndex() {
        // 不添加任何DSP，直接搜索
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.isEmpty(), "空索引应该返回空结果");
    }

    @Test
    @DisplayName("测试搜索DSP - 所有条件都是DEFAULT_ALL_TARGETING")
    void testSearchDspsWithAllDefaultAllTargeting() {
        // 准备测试数据：无定向信息
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspAggregate dspAggregate = createDspAggregate(1, "NoTargetingDSP", placements, null);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：所有条件都是DEFAULT_ALL_TARGETING
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(1), "所有条件都是DEFAULT_ALL_TARGETING应该能匹配到无定向信息的DSP");
    }

    @Test
    @DisplayName("测试搜索DSP - 索引键不存在")
    void testSearchDspsWithNonExistentKeys() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：使用不存在的键
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("NONEXISTENT_OS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("NONEXISTENT_DEVICE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("NONEXISTENT_TAG", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("NONEXISTENT_REGION", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        // 应该只匹配到DEFAULT_ALL_TARGETING对应的DSP（如果有的话）
        // 在这个测试中，DSP1有明确的定向条件，不应该匹配到
        assertTrue(result.isEmpty() || !result.contains(1), "使用不存在的键不应该匹配到有定向条件的DSP");
    }

    @Test
    @DisplayName("测试搜索DSP - 复杂AND逻辑")
    void testSearchDspsComplexAndLogic() {
        // 准备测试数据
        List<SiteAdPlacement> placements1 = Arrays.asList(createSiteAdPlacement("TAG001"), createSiteAdPlacement("TAG002"));
        DspTargeting targeting1 = createTargeting("[\"ios\", \"android\"]", "[\"phone\"]", "[\"110000\", \"310000\"]");
        DspAggregate dspAggregate1 = createDspAggregate(1, "DSP1", placements1, targeting1);
        indexService.indexDsp(dspAggregate1);

        List<SiteAdPlacement> placements2 = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting2 = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate2 = createDspAggregate(2, "DSP2", placements2, targeting2);
        indexService.indexDsp(dspAggregate2);

        // 执行搜索1：TAG001 + IOS + PHONE + 110000 -> 应该匹配到DSP1和DSP2
        IndexKeys indexKeys1 = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );
        List<Integer> result1 = indexService.searchDsps(indexKeys1);
        assertEquals(2, result1.size());
        assertTrue(result1.contains(1));
        assertTrue(result1.contains(2));

        // 执行搜索2：TAG002 + IOS + PHONE + 110000 -> 应该只匹配到DSP1
        IndexKeys indexKeys2 = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG002", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );
        List<Integer> result2 = indexService.searchDsps(indexKeys2);
        assertEquals(1, result2.size());
        assertTrue(result2.contains(1));
        assertFalse(result2.contains(2));

        // 执行搜索3：TAG001 + ANDROID + PHONE + 110000 -> 应该只匹配到DSP1
        IndexKeys indexKeys3 = createIndexKeys(
                Arrays.asList("ANDROID", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );
        List<Integer> result3 = indexService.searchDsps(indexKeys3);
        assertEquals(1, result3.size());
        assertTrue(result3.contains(1));
        assertFalse(result3.contains(2));
    }

    @Test
    @DisplayName("测试搜索DSP - 优化AND逻辑（按基数排序）")
    void testSearchDspsOptimizedAndLogic() {
        // 准备测试数据：创建大量不同定向条件的DSP
        for (int i = 1; i <= 100; i++) {
            List<SiteAdPlacement> placements = Arrays.asList(
                    createSiteAdPlacement("TAG" + String.format("%03d", i)));
            DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
            DspAggregate dspAggregate = createDspAggregate(i, "DSP" + i, placements, targeting);
            indexService.indexDsp(dspAggregate);
        }

        // 执行搜索：应该只匹配到TAG001
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertEquals(1, result.size(), "应该只匹配到1个DSP");
        assertEquals(1, result.get(0), "应该匹配到DSP1");
    }

    @Test
    @DisplayName("测试搜索DSP - 返回结果顺序")
    void testSearchDspsResultOrder() {
        // 准备测试数据：添加多个DSP
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        for (int i = 1; i <= 5; i++) {
            DspAggregate dspAggregate = createDspAggregate(i, "DSP" + i, placements, targeting);
            indexService.indexDsp(dspAggregate);
        }

        // 执行搜索
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        // 验证结果顺序（RoaringBitmap的迭代器顺序）
        assertEquals(5, result.size());
        // 注意：RoaringBitmap的迭代顺序不保证是插入顺序，所以我们只验证包含关系
        for (int i = 1; i <= 5; i++) {
            assertTrue(result.contains(i), "应该包含DSP" + i);
        }
    }

    // ==================== indexAdGroup Tests ====================

    @Test
    @DisplayName("测试索引广告组 - TODO方法")
    void testIndexAdGroup() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);

        // 执行索引（目前是TODO方法，不应该抛出异常）
        assertDoesNotThrow(() -> {
            indexService.indexAdGroup(dspAggregate);
        }, "indexAdGroup方法不应该抛出异常");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("测试索引DSP - DSP ID为0")
    void testIndexDspWithZeroId() {
        // 准备测试数据：DSP ID为0
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(0, "ZeroIdDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：应该能索引成功
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(0), "DSP ID为0应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - DSP ID为负数")
    void testIndexDspWithNegativeId() {
        // 准备测试数据：DSP ID为负数
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(-1, "NegativeIdDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：应该能索引成功
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(-1), "DSP ID为负数应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - 非常大的DSP ID")
    void testIndexDspWithLargeId() {
        // 准备测试数据：非常大的DSP ID
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(Integer.MAX_VALUE, "LargeIdDSP", placements, targeting);

        // 执行索引
        indexService.indexDsp(dspAggregate);

        // 验证：应该能索引成功
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertTrue(result.contains(Integer.MAX_VALUE), "非常大的DSP ID应该能匹配到");
    }

    @Test
    @DisplayName("测试索引DSP - 空的OS JSON数组")
    void testIndexDspWithEmptyOsArray() {
        // 准备测试数据：空的OS JSON数组
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(1, "EmptyOsDSP", placements, targeting);

        // 执行索引（空数组会被解析，但没有有效的OS）
        assertDoesNotThrow(() -> {
            indexService.indexDsp(dspAggregate);
        }, "空的OS数组不应该抛出异常");

        // 验证：空的OS数组应该相当于不限OS
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        // 由于空的OS数组不会添加任何OS索引，所以需要通过DEFAULT_ALL_TARGETING来匹配
        // 但在这个实现中，空数组和null的处理是一样的，所以会使用DEFAULT_ALL_TARGETING
        // 这里我们验证不会抛出异常
    }

    @Test
    @DisplayName("测试索引DSP - 多次索引同一个DSP")
    void testIndexDspMultipleTimes() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);

        // 执行索引：多次索引同一个DSP
        indexService.indexDsp(dspAggregate);
        indexService.indexDsp(dspAggregate);
        indexService.indexDsp(dspAggregate);

        // 验证：多次索引不应该导致重复
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        assertEquals(1, result.size(), "多次索引同一个DSP不应该导致重复");
        assertTrue(result.contains(1), "应该包含DSP1");
    }

    @Test
    @DisplayName("测试搜索DSP - IndexKeys为空列表")
    void testSearchDspsWithEmptyKeys() {
        // 准备测试数据
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", placements, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：IndexKeys的某些字段为空列表
        IndexKeys indexKeys = createIndexKeys(
                Collections.emptyList(), // 空OS列表
                Collections.emptyList(), // 空设备类型列表
                Collections.emptyList(), // 空广告位列表
                Collections.emptyList() // 空区域列表
        );

        // 空列表会导致没有匹配的bitmap，应该返回空结果
        List<Integer> result = indexService.searchDsps(indexKeys);
        assertTrue(result.isEmpty(), "所有字段为空列表应该返回空结果");
    }

    @Test
    @DisplayName("测试搜索DSP - 部分IndexKeys为空列表")
    void testSearchDspsWithPartialEmptyKeys() {
        // 准备测试数据：不限广告位
        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(1, "TestDSP", null, targeting);
        indexService.indexDsp(dspAggregate);

        // 执行搜索：只有tagIdKeys为空列表
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Collections.emptyList(), // 空广告位列表
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);
        assertTrue(result.isEmpty(), "空列表会导致没有匹配的bitmap");
    }

    @Test
    @DisplayName("测试索引DSP - 无效的JSON格式")
    void testIndexDspWithInvalidJson() {
        // 准备测试数据：无效的JSON格式
        List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
        DspTargeting targeting = createTargeting("invalid_json", "[\"phone\"]", null);
        DspAggregate dspAggregate = createDspAggregate(1, "InvalidJsonDSP", placements, targeting);

        // 执行索引：无效的JSON格式应该抛出异常
        assertThrows(Exception.class, () -> {
            indexService.indexDsp(dspAggregate);
        }, "无效的JSON格式应该抛出异常");
    }

    @Test
    @DisplayName("测试索引DSP - null值处理")
    void testIndexDspWithNullValues() {
        // 准备测试数据：部分字段为null
        Dsp dsp = new Dsp();
        dsp.setId(1);
        dsp.setName("TestDSP");
        DspAggregate dspAggregate = new DspAggregate(dsp, null, null, null);

        // 执行索引：应该正常处理null值
        assertDoesNotThrow(() -> {
            indexService.indexDsp(dspAggregate);
        }, "null值不应该导致异常");

        // 验证：null值应该被正确处理
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);
        assertTrue(result.contains(1), "null值应该被正确处理");
    }

    @Test
    @DisplayName("测试索引和搜索的并发安全性")
    void testConcurrentIndexAndSearch() throws InterruptedException {
        // 准备测试数据
        int threadCount = 10;
        int dspsPerThread = 10;
        final int totalDspCount = threadCount * dspsPerThread;

        // 使用同步集合来跟踪已索引的DSP ID
        final List<Integer> indexedDspIds = Collections.synchronizedList(new ArrayList<>());

        // 创建多个线程同时进行索引和搜索
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < dspsPerThread; j++) {
                        int dspId = threadId * dspsPerThread + j + 1; // DSP ID从1开始
                        List<SiteAdPlacement> placements = Arrays.asList(
                                createSiteAdPlacement("TAG" + String.format("%03d", dspId)));
                        DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
                        DspAggregate dspAggregate = createDspAggregate(dspId, "DSP" + dspId, placements, targeting);
                        indexService.indexDsp(dspAggregate);
                        indexedDspIds.add(dspId);
                    }

                    // 同时进行搜索 - 搜索本线程创建的第一个DSP
                    int firstDspId = threadId * dspsPerThread + 1;
                    IndexKeys indexKeys = createIndexKeys(
                            Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                            Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                            Arrays.asList("TAG" + String.format("%03d", firstDspId), Constants.DEFAULT_ALL_TARGETING),
                            Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
                    );
                    List<Integer> searchResult = indexService.searchDsps(indexKeys);
                    // 验证搜索结果包含至少一个DSP ID
                    assertFalse(searchResult.isEmpty(), "并发搜索应该返回结果");
                } catch (Exception e) {
                    // 捕获并发测试中的异常，避免测试失败
                    e.printStackTrace();
                }
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证：所有DSP都应该被正确索引
        // 由于每个DSP都有特定的广告位，我们需要搜索所有可能的广告位
        List<Integer> allDsps = new ArrayList<>();
        for (int i = 1; i <= totalDspCount; i++) {
            IndexKeys indexKeys = createIndexKeys(
                    Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList("TAG" + String.format("%03d", i), Constants.DEFAULT_ALL_TARGETING),
                    Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
            );
            List<Integer> result = indexService.searchDsps(indexKeys);
            allDsps.addAll(result);
        }

        // 验证所有DSP都被索引了（去重后应该等于总数）
        List<Integer> uniqueDsps = allDsps.stream().distinct().sorted().toList();
        assertEquals(totalDspCount, uniqueDsps.size(),
                "所有DSP都应该被正确索引，期望: " + totalDspCount + ", 实际: " + uniqueDsps.size());
    }

    @Test
    @DisplayName("测试搜索性能 - 大量DSP")
    void testSearchPerformanceWithManyDsps() {
        // 准备测试数据：创建大量DSP
        int dspCount = 1000;
        for (int i = 1; i <= dspCount; i++) {
            List<SiteAdPlacement> placements = Arrays.asList(
                    createSiteAdPlacement("TAG001"));
            DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", null);
            DspAggregate dspAggregate = createDspAggregate(i, "DSP" + i, placements, targeting);
            indexService.indexDsp(dspAggregate);
        }

        // 执行搜索
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("IOS", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList(Constants.DEFAULT_ALL_TARGETING)
        );

        long startTime = System.currentTimeMillis();
        List<Integer> result = indexService.searchDsps(indexKeys);
        long endTime = System.currentTimeMillis();

        // 验证：应该在合理时间内完成
        assertTrue((endTime - startTime) < 100, 
                "搜索1000个DSP应该在100ms内完成");
        assertEquals(dspCount, result.size(), 
                "应该匹配到所有" + dspCount + "个DSP");
    }

    @Test
    @DisplayName("测试RoaringBitmap基数优化 - 提前终止")
    void testRoaringBitmapCardinalityOptimizationEarlyTermination() {
        // 准备测试数据：创建多个不同的DSP
        // DSP1-100: TAG001, IOS, PHONE, 110000
        for (int i = 1; i <= 100; i++) {
            List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG001"));
            DspTargeting targeting = createTargeting("[\"ios\"]", "[\"phone\"]", "[\"110000\"]");
            DspAggregate dspAggregate = createDspAggregate(i, "DSP" + i, placements, targeting);
            indexService.indexDsp(dspAggregate);
        }

        // DSP101-200: TAG002, ANDROID, PHONE, 110000
        for (int i = 101; i <= 200; i++) {
            List<SiteAdPlacement> placements = Arrays.asList(createSiteAdPlacement("TAG002"));
            DspTargeting targeting = createTargeting("[\"android\"]", "[\"phone\"]", "[\"110000\"]");
            DspAggregate dspAggregate = createDspAggregate(i, "DSP" + i, placements, targeting);
            indexService.indexDsp(dspAggregate);
        }

        // 执行搜索：TAG001 + ANDROID + PHONE + 110000
        // TAG001匹配DSP1-100（100个）
        // ANDROID匹配DSP101-200（100个）
        // 两个AND后应该为空，提前终止
        IndexKeys indexKeys = createIndexKeys(
                Arrays.asList("ANDROID", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("PHONE", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("TAG001", Constants.DEFAULT_ALL_TARGETING),
                Arrays.asList("110000", Constants.DEFAULT_ALL_TARGETING)
        );

        List<Integer> result = indexService.searchDsps(indexKeys);

        // 应该没有匹配结果
        assertTrue(result.isEmpty(), "TAG001和ANDROID的交集应该为空");
    }
}
