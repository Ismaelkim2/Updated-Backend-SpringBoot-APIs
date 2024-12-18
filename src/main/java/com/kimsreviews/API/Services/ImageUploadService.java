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
    private static final String CLIENT_ID = "your-client-id";
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);

    public Map<String, Object> uploadImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("Authorization", "Client-ID " + CLIENT_ID);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new CustomByteArrayResource(file.getBytes(), file.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(IMGUR_API_URL, requestEntity, Map.class);

            logger.info("Image uploaded successfully: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.error("Exception during image upload", e);
            throw new RuntimeException("Image upload failed", e);
        }
    }

    private static class CustomByteArrayResource extends ByteArrayResource {
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


