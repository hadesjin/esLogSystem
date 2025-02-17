package com.example.logquerysystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 解决跨域资源共享问题。
     *
     * @param registry 用于配置CORS（跨源资源共享）设置的注册中心。
     *
     * 此方法通过配置CORS设置来解决跨域资源共享问题。
     * 它允许来自特定来源的请求访问该应用，同时指定允许的HTTP方法、请求头等。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // 允许前端开发服务器访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 预检请求的有效期，单位为秒
    }
}