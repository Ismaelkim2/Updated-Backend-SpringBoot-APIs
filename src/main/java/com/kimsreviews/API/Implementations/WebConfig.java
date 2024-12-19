package com.kimsreviews.API.Implementations;

import com.kimsreviews.API.Services.ImageUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${imgur.client-id}")
    private String imgurClientId;



    @Bean
    public RestTemplate restTemplate() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new FormHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());

        // Create the factory with timeouts
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        // Return a RestTemplate that uses the factory
        return new RestTemplate(factory);
    }


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
}
