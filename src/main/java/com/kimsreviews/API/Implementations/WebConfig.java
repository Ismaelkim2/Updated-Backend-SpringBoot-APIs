package com.kimsreviews.API.Implementations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://brishkimecoeggs.onrender.com",
                        frontendUrl != null ? frontendUrl : "http://localhost:4200" // Default fallback
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (uploadDir != null && !uploadDir.isEmpty()) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + uploadDir + "/");
        } else {
            throw new IllegalArgumentException("Upload directory path cannot be null or empty.");
        }
    }
}
