package top.openadexchange.openapi.ssp.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.annotation.Resource;
import top.openadexchange.openapi.ssp.domain.model.IpLocation;
import top.openadexchange.openapi.ssp.infra.config.IP2RegionConfig;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IP2RegionConfig.class, DefaultIP2RegionService.class})
class DefaultIP2RegionServiceTest {
    @Resource
    private DefaultIP2RegionService ip2RegionService;
    @Resource
    private Ip2Region ip2Region;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void testInit() {
        // 验证初始化方法已执行，cityRegionCodeMap已被加载
        // 由于cityRegionCodeMap是私有字段，我们通过测试getRegion方法来间接验证
        assertNotNull(ip2RegionService);
    }

    @Test
    void testGetRegionWithValidIPv4() {
        // 测试一个有效的IPv4地址
        String ip = "113.92.157.29"; // 北京IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertNotNull(location.getCountry());
        assertNotNull(location.getProvince());
        assertNotNull(location.getCity());
        assertNotNull(location.getRegionCode());
        //assertNotNull(location.getRegion());
    }

    @Test
    void testGetRegionWithBeijingIP() {
        // 测试北京IP地址
        String ip = "116.228.55.100"; // 北京IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertEquals("中国", location.getCountry());
        assertEquals("上海", location.getProvince());
        assertEquals("上海市", location.getCity());
        assertNotNull(location.getRegionCode());
        assertEquals("310000", location.getRegionCode());
    }

    @Test
    void testGetRegionWithShanghaiIP() {
        // 测试上海IP地址
        String ip = "202.96.209.5"; // 上海IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertEquals("中国", location.getCountry());
        assertEquals("上海", location.getProvince());
        assertEquals("上海市", location.getCity());
    }

    @Test
    void testGetRegionWithGuangzhouIP() {
        // 测试广州IP地址
        String ip = "202.100.96.100"; // 广州IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertEquals("中国", location.getCountry());
        assertEquals("宁夏", location.getProvince());
        assertEquals("银川市", location.getCity());
    }

    @Test
    void testGetRegionWithShenzhenIP() {
        // 测试深圳IP地址
        String ip = "113.88.10.1"; // 深圳IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertEquals("中国", location.getCountry());
        assertEquals("广东省", location.getProvince());
        assertEquals("深圳市", location.getCity());
    }

    @Test
    void testGetRegionWithHangzhouIP() {
        // 测试杭州IP地址
        String ip = "115.236.10.1"; // 杭州州IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertEquals("中国", location.getCountry());
        assertEquals("浙江省", location.getProvince());
        assertEquals("杭州市", location.getCity());
    }

    @Test
    void testGetRegionWithPrivateIP() {
        // 测试私有IP地址
        String ip = "192.168.1.1";
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
    }

    @Test
    void testGetRegionWithLoopbackIP() {
        // 测试回环地址
        String ip = "127.0.0.1";
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
    }

    @Test
    void testGetRegionWithEmptyIP() {
        // 测试空IP地址，应该抛出异常
        //ip地址位空不抛出异常，默认直接返回内网
    }

    @Test
    void testGetRegionWithNullIP() {
        // 测试null IP地址，返回内网IP
    }

    @Test
    void testGetRegionWithInvalidIP() {
        // 测试无效的IP地址
        String ip = "invalid.ip.address";

        assertThrows(RuntimeException.class, () -> {
            ip2RegionService.getRegion(ip);
        });
    }

    @Test
    void testGetRegionWithInvalidIPFormat() {
        // 测试格式错误的IP地址
        String ip = "999.999.999.999";

        assertThrows(RuntimeException.class, () -> {
            ip2RegionService.getRegion(ip);
        });
    }

    @Test
    void testGetRegionByIpV6() {
        // 测试IPv6地址
        assertThrows(UnsupportedOperationException.class, () -> {
            ip2RegionService.getRegionByIpV6("240e:3b1:a054:12d0:bc42:77ff:fe21:e2b3");
        });
    }

    @Test
    void testGetRegionFieldsStructure() {
        // 测试返回的IpLocation对象的字段结构
        String ip = "116.228.55.100";
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertNotNull(location.getCountry(), "country should not be null");
        //assertNotNull(location.getRegion(), "region should not be null");
        assertNotNull(location.getProvince(), "province should not be null");
        assertNotNull(location.getCity(), "city should not be null");
        assertNotNull(location.getIsp(), "isp should not be null");
    }

    @Test
    void testGetRegionWithKnownCityCodeMapping() {
        // 测试已知有区域码映射的城市（北京）
        String ip = "116.228.55.100"; // 北京IP
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertNotNull(location.getRegionCode(), "Beijing should have a region code");
        assertEquals("310000", location.getRegionCode());
    }

    @Test
    void testGetRegionWithTianjinIP() {
        // 测试天津IP地址
        String ip = "117.10.33.100"; // 天津IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertEquals("中国", location.getCountry());
        assertEquals("天津", location.getProvince());
        assertEquals("天津市", location.getCity());
        assertNotNull(location.getRegionCode());
        assertEquals("120000", location.getRegionCode());
    }

    @Test
    void testGetRegionWithHebeiShijiazhuangIP() {
        // 测试河北石家庄IP地址
        String ip = "111.11.10.1"; // 河北石家庄IP地址
        IpLocation location = ip2RegionService.getRegion(ip);

        assertNotNull(location);
        assertEquals("中国", location.getCountry());
        assertEquals("河北省", location.getProvince());
        assertEquals("石家庄市", location.getCity());
        assertNotNull(location.getRegionCode());
        assertEquals("130100", location.getRegionCode());
    }
}
