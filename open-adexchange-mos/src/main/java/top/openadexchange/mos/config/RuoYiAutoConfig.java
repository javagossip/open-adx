package top.openadexchange.mos.config;

import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"com.ruoyi"})
@MapperScan(basePackages = {"com.ruoyi.**.mapper"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class RuoYiAutoConfig {
    @PostConstruct
    public void init() {
        System.out.println("RuoAutoYiConfig init");
    }
}
