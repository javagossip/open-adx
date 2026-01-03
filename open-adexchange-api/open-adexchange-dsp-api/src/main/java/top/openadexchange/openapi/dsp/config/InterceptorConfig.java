package top.openadexchange.openapi.dsp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.openadexchange.openapi.dsp.auth.AuthInterceptor;

import jakarta.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 为所有DSP API接口添加认证拦截器，排除健康检查等公共接口
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/v1/**")
                .excludePathPatterns("/health", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");
    }
}