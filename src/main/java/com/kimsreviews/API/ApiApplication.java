package com.kimsreviews.API;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication(scanBasePackages = "com.kimsreviews.API")
@EnableScheduling
public class ApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@PostConstruct
	public void createUploadsDirectory() {
		File uploadDir = new File("/uploads");
		if (!uploadDir.exists()) {
			boolean created = uploadDir.mkdirs();
			if (!created) {
				throw new RuntimeException("Failed to create /uploads directory");
			}
		}
	}

}
