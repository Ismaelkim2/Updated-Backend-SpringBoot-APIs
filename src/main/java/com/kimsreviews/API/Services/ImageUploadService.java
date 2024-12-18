package com.kimsreviews.API.Services;

import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class ImageUploadService {
    private final RestTemplate restTemplate;
    private String clientId;
    private final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);

    public ImageUploadService(RestTemplate restTemplate, String clientId) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
    }

    public String uploadImage(MultipartFile imageFile) throws IOException {
        byte[] imageBytes = imageFile.getBytes();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new CustomByteArrayResource(imageBytes, imageFile.getOriginalFilename()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Client-ID " + clientId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = "https://api.imgur.com/3/upload";
        try {
            // Perform the upload
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            logger.debug("Imgur response: {}", response.getBody());

            // Parse the Imgur API response (extract the image URL)
            return extractImageUrlFromResponse(response.getBody());
        } catch (HttpClientErrorException e) {
            // Handle HTTP client errors (e.g., 4xx errors)
            logger.error("HTTP error during Imgur upload: {} - {}", e.getStatusCode(), e.getMessage());
            throw e;
        } catch (ResourceAccessException e) {
            // Handle I/O errors
            logger.error("I/O error during Imgur upload: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Method to extract image URL from Imgur API response
    private String extractImageUrlFromResponse(String responseBody) {
        try {
            // Use Jackson to parse the response and extract the image URL
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            return jsonResponse.get("data").get("link").asText();
        } catch (IOException e) {
            logger.error("Failed to parse Imgur response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Imgur response", e);
        }
    }

    // Custom ByteArrayResource to send image file as multipart
    public static class CustomByteArrayResource extends ByteArrayResource {
        private final String filename;

        public CustomByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}





