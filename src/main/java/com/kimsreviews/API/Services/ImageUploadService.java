package com.kimsreviews.API.Services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

@Service
public class ImageUploadService {

    private static final String IMGUR_API_URL = "https://api.imgur.com/3/upload";
    private static final String CLIENT_ID = "ce427f04934e505";

    public String uploadImage(MultipartFile image) throws Exception {
        // Set up the HTTP client
        RestTemplate restTemplate = new RestTemplate();

        // Set headers for the request (use your Imgur Client-ID)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + CLIENT_ID);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Prepare the file as a multipart body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getBytes());

        // Make the request to the Imgur API
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(IMGUR_API_URL, HttpMethod.POST, entity, Map.class);

        // Extract the URL of the uploaded image from the response
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            // Ensure "data" is a map and extract the URL
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            return (String) data.get("link");  // Correctly extract the image URL
        }

        throw new Exception("Image upload failed");
    }
}
