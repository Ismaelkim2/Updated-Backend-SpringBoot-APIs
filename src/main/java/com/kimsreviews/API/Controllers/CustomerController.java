package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.CustomerService;
import com.kimsreviews.API.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Value("${file.upload-dir}")
    private String IMAGE_DIRECTORY;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PostMapping
    public Customer createCustomer(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("image") MultipartFile image) throws IOException {

        // Validate input parameters
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        // Create a new Customer object
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        // Handle the image file
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            customer.setImage(imageUrl);
        } else {
            // Handle case where no image is uploaded
            customer.setImage(null); // or provide a default image URL
        }

        // Save the customer and return the result
        return customerService.saveCustomer(customer);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String contentType = image.getContentType();
        if (!contentType.startsWith("image")) {
            throw new IllegalArgumentException("File must be an image");
        }

        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_DIRECTORY, fileName);
        Files.copy(image.getInputStream(), filePath);

        // Return a URL that the Angular app can access
        return "uploads/" + fileName;
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id); // Set the ID for the update operation
        return customerService.saveCustomer(customer);
    }
}
