package com.kimsreviews.API;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@PostConstruct
	public void createUploadsDirectory() {
		File uploadDir = new File("uploads");
		if (!uploadDir.exists()) {
			boolean created = uploadDir.mkdirs();
			if (!created) {
				throw new RuntimeException("Failed to create uploads directory");
			}
		}
	}

	@Bean
	public WebMvcConfigurer corsConfigurer(@Value("${frontend.url:http://localhost:4200}") String frontendUrl) {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(
								"https://dashing-squirrel-91b6d0.netlify.app",
								"https://brishkimecoeggs.onrender.com",
								frontendUrl
						)
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true)
						.maxAge(3600);
			}
		};
	}

	@Bean
	public RestTemplate restTemplate() {
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new FormHttpMessageConverter());
		converters.add(new StringHttpMessageConverter());

		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(5000);

		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.setMessageConverters(converters);
		return restTemplate;
	}
}
