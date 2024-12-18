package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.CustomerRepository;
import com.kimsreviews.API.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Customer saveCustomer(Customer customer, MultipartFile image) throws Exception {
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(image).toString();
            customer.setImage(imageUrl);
        }
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {

        return customerRepository.findById(id).orElse(null);
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer, MultipartFile image) throws Exception {
        Optional<Customer> existingCustomerOpt = customerRepository.findById(id);
        if (existingCustomerOpt.isPresent()) {
            Customer existingCustomer = existingCustomerOpt.get();

            // Update fields
            existingCustomer.setName(updatedCustomer.getName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setPhone(updatedCustomer.getPhone());

            // Update image if a new one is provided
            if (image != null && !image.isEmpty()) {
                String imageUrl = imageUploadService.uploadImage(image).toString();
                existingCustomer.setImage(imageUrl);
            }

            return customerRepository.save(existingCustomer);
        } else {
            throw new Exception("Customer with ID " + id + " not found");
        }
    }
}
