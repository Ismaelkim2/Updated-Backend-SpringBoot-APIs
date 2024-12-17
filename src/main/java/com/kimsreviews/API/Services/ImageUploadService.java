package com.kimsreviews.API.Services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Base64;
import java.util.Map;

@Service
public class ImageUploadService {

    private static final String IMGUR_API_URL = "https://api.imgur.com/3/upload";
    private static final String CLIENT_ID = "ce427f04934e505";

    public String uploadImage(MultipartFile image) throws Exception {
        // 1. Validate that the image is not null or empty
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required.");
        }

        // 2. Convert the image to Base64 format
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

        // 3. Set up RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // 4. Configure headers for Imgur API
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + CLIENT_ID);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 5. Prepare the request body (Base64 image string)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64Image);

        // 6. Create the HTTP entity containing headers and body
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // 7. Execute the POST request
        ResponseEntity<Map> response = restTemplate.exchange(IMGUR_API_URL, HttpMethod.POST, entity, Map.class);

        // 8. Process the response
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();

            if (responseBody.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data.containsKey("link")) {
                    return (String) data.get("link"); // Return the image URL
                }
            }
        }

        // 9. Handle unsuccessful uploads
        throw new Exception("Image upload failed. Response: " + response.getBody());
    }
}
