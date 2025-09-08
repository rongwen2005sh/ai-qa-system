package com.ai.qa.user.infrastructure.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置类
 * 用于配置应用程序的安全策略，包括认证、授权、密码加密等
 *
 * @author Rong Wen
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter  jwtAuthFilter;

    /**
     * 配置密码编码器Bean
     * 使用BCrypt强哈希算法进行密码加密和验证
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器Bean
     * 用于处理用户认证请求
     * @param config 认证配置对象，由Spring自动注入
     * @return AuthenticationManager实例
     * @throws Exception 可能抛出的异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置安全过滤器链
     * 定义HTTP请求的安全策略和过滤规则
     * @param http HttpSecurity对象，用于构建安全配置
     * @return SecurityFilterChain实例
     * @throws Exception 可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF（前后端分离场景）
                .csrf(csrf -> csrf.disable())
                // 不使用Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置URL权限
                .authorizeHttpRequests(auth -> auth
                        // 允许匿名访问的接口（登录、注册、Swagger）
                        .antMatchers("/api/users/login", "/api/users/register").permitAll()
                        .antMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // 其他接口需要认证
                        .anyRequest().authenticated()
                )
                // 添加JWT过滤器（在用户名密码过滤器之前）
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}