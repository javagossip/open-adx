package com.hexunion.mos.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import jakarta.annotation.PostConstruct;

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
