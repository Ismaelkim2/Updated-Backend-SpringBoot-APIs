package com.kimsreviews.API.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ImageUploadService {

    private static final String IMGUR_API_URL = "https://api.imgur.com/3/upload";
    private static final String CLIENT_ID = "ce427f04934e505";
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);

    public String uploadImage(MultipartFile image) throws Exception {
        if (image == null || image.isEmpty()) {
            logger.error("Image file is null or empty.");
            throw new IllegalArgumentException("Image file is required.");
        }

        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + CLIENT_ID);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Prepare request body (multipart/form-data)
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename(); // Set the file name for the request
                }
            });

            // Create request entity
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            logger.info("Sending HTTP request to Imgur API...");

            // Send the request using RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(IMGUR_API_URL, HttpMethod.POST, entity, Map.class);

            // Process the response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    if (data.containsKey("link")) {
                        String imageUrl = (String) data.get("link");
                        logger.info("Image uploaded successfully. URL: " + imageUrl);
                        return imageUrl;
                    }
                }
            }

            // If the response is not valid, throw an exception
            logger.error("Image upload failed. Response: " + response.getBody());
            throw new Exception("Image upload failed. Response: " + response.getBody());
        } catch (Exception e) {
            logger.error("Exception during image upload", e);
            throw new Exception("Image upload failed due to: " + e.getMessage(), e);
        }
    }
}
