package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.CustomerService;
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

    @PutMapping("/{id}")
    public Customer updateCustomer(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {

        // Fetch the existing customer
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        // Update customer details
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        // Save the updated customer along with the image if provided
        return customerService.saveCustomer(customer, image);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @ModelAttribute Customer customer,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer, image);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
