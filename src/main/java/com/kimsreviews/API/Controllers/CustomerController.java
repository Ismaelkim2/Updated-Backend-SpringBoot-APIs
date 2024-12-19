package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.CustomerService;
import com.kimsreviews.API.Services.ImageUploadService;
import com.kimsreviews.API.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    private ImageUploadService imageUploadService;

    @PostMapping
    public Customer createCustomer(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("image") MultipartFile image) throws Exception {

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

        // Save the customer along with the image
        return customerService.saveCustomer(customer, image);
    }


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


    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        // Create an updated customer object with the provided data
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName(name);
        updatedCustomer.setEmail(email);
        updatedCustomer.setPhone(phone);

        // Call the service to update the customer
        try {
            Customer customer = customerService.updateCustomer(id, updatedCustomer, image);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}





