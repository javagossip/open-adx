package top.openadexchange.mos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // 定义 SecurityScheme
        SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-Open-Ad-Exchange-Token");

        // 将 SecurityScheme 添加到 Components 中
        Components components = new Components().addSecuritySchemes("OpenAdExchangeTokenAuth", securityScheme);

        return new OpenAPI().components(components)
                .addServersItem(new Server().url("/"))
                .info(new Info().title("Open Ad Exchange mos API Document")
                        .description("Open Ad Exchange mos API Document")
                        .version("v1"))  // This is the API version, not the OpenAPI version
                .externalDocs(new ExternalDocumentation().description("API 文档地址").url("/"))
                .addSecurityItem(new SecurityRequirement().addList("OpenAdExchangeTokenAuth"));
    }
}
