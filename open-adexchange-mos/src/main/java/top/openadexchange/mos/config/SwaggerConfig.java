package com.hexunion.mos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // 定义 SecurityScheme
        SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-HelxPay-Token");

        // 将 SecurityScheme 添加到 Components 中
        Components components = new Components().addSecuritySchemes("helxPayTokenAuth", securityScheme);

        return new OpenAPI().components(components)
                .addServersItem(new Server().url("/"))
                .info(new Info().title("HelxPay dashboard API文档")
                        .description("HelxPay dashboard API文档")
                        .version("v1"))  // This is the API version, not the OpenAPI version
                .externalDocs(new ExternalDocumentation().description("API 文档地址").url("/"))
                .addSecurityItem(new SecurityRequirement().addList("helxPayTokenAuth"));
    }
}
