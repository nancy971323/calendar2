package com.company.calendar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置類
 * <p>
 * 配置Web相關設置，包括前端路由處理、靜態資源處理和CORS設置。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置視圖控制器
     * <p>
     * 將前端路由映射到index.html，以支持Vue.js的前端路由。
     * </p>
     * 
     * @param registry 視圖控制器註冊器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 將前端路由映射到index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/calendar").setViewName("forward:/index.html");
        registry.addViewController("/admin").setViewName("forward:/index.html");
        registry.addViewController("/admin/**").setViewName("forward:/index.html");
    }
    
    /**
     * 配置靜態資源處理
     * <p>
     * 設置靜態資源的位置。
     * </p>
     * 
     * @param registry 資源處理器註冊器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
    
    /**
     * 配置CORS
     * <p>
     * 設置跨域資源共享策略。
     * </p>
     * 
     * @param registry CORS註冊器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}