package com.ai.qa.user.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 * 用于配置API文档的生成和展示
 *
 * @author Rong Wen
 * @version 1.0
 */
@Configuration
public class SwaggerConfig {

    /**
     * 自定义OpenAPI配置
     * 配置API文档的基本信息、服务器地址和安全认证方案
     * @return OpenAPI对象，包含所有API文档配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Micro Service")
                        .description("用户管理微服务")
                        .version("1.0.0")
                )
                .servers(java.util.Arrays.asList(
                        new Server().url("http://localhost:8081").description("开发环境")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT认证令牌")));
    }
}