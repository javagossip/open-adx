package top.openadexchange.openapi.ssp.infra.config;

import java.io.InputStream;

import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class IP2RegionConfig {

    @Bean
    public Ip2Region ip2Region() {
        try {
            final Config v4Config = Config.custom()
                    .setCachePolicy(Config.BufferCache)     // 指定缓存策略:  NoCache / VIndexCache / BufferCache
                    // 设置初始化的查询器数量
                    .setXdbInputStream(getXdbInputStream("xdb/ip2region_v4.xdb"))   // 设置 v4 xdb 文件的 inputstream 对象
                    .asV4();    // 指定为 v4 配置
            return Ip2Region.create(v4Config, null);
        } catch (Exception ex) {
            log.error("IP2Region init error: {}", ex.getMessage());
        }
        return null;
    }

    public InputStream getXdbInputStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
}
