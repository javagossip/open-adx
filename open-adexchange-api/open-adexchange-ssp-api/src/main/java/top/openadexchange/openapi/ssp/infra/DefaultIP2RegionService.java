package top.openadexchange.openapi.ssp.infra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lionsoul.ip2region.service.Ip2Region;

import com.chaincoretech.epc.annotation.Extension;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import top.openadexchange.openapi.ssp.domain.gateway.IP2RegionService;
import top.openadexchange.openapi.ssp.domain.model.IpLocation;

@Extension(keys = {"ip2region", "default"})
@Slf4j
public class DefaultIP2RegionService implements IP2RegionService {

    private static final String CITY_CODE_MAP_CSV = "xdb/city_code_map.csv";
    private static final Map<String, String> cityRegionCodeMap = new ConcurrentHashMap<>();

    @Resource
    private Ip2Region ip2Region;

    @PostConstruct
    public void init() {
        log.info("初始化cityRegionCodeMap...");
        //异步加载城市->区域码的映射关系
        //读取城市->区域码的映射文件 city_code_map.csv, 初始化cityRegionCodeMap
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(CITY_CODE_MAP_CSV); BufferedReader reader =
                new BufferedReader(new InputStreamReader(
                inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    cityRegionCodeMap.put(parts[0].trim(), parts[1].trim());
                }
            }
            log.info("cityRegionCodeMap初始化完成，包含{}个映射关系", cityRegionCodeMap.size());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load city region code map", ex);
        }
    }

    @Override
    public IpLocation getRegion(String ip) {
        try {
            String ipLocationString = ip2Region.search(ip);
            String[] ipLocationArray = ipLocationString.split("\\|");
            String regionCode = cityRegionCodeMap.get(String.format("%s-%s", ipLocationArray[1], ipLocationArray[2]));
            return new IpLocation(ipLocationArray[0],
                    ipLocationArray[1],
                    ipLocationArray[2],
                    ipLocationArray[3],
                    regionCode);
        } catch (Exception ex) {
            log.error("Failed to get region for IP: {}", ip, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public IpLocation getRegionByIpV6(String ipv6) {
        throw new UnsupportedOperationException("IPv6 is not supported");
    }
}
