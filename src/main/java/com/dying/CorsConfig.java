package com.dying;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;  
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;  
  
/**
 * @author daylight
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {  
  
    @Override  
    public void addCorsMappings(CorsRegistry registry) {
        // 添加映射路径  
        registry.addMapping("/**")
                // 允许哪些域的请求，星号代表允许所有
                .allowedOrigins("http://localhost:9191","https://bc.seestars.top:9191","https://bc.seestars.top")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("*")
                // 是否发送cookie
                .allowCredentials(true)
                // 预检间隔时间
                .maxAge(168000);
    }  
}