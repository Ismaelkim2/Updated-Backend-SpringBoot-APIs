package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.WorkerService;
import com.kimsreviews.API.models.Worker;
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
@RequestMapping("/api/workers")
@CrossOrigin(origins = "http://localhost:4200")
public class WorkerController {

    private final WorkerService workerService;

    @Value("${file.upload-dir}")
    private String IMAGE_DIRECTORY;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public List<Worker> getAllWorkers() {
        return workerService.getAllWorkers();
    }

    @PostMapping
    public Worker saveWorker(
            @RequestParam("name") String name,
            @RequestParam("role") String role,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("salary") double salary,
            @RequestParam("status") String status,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        // Validate required parameters
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");
        if (role == null || role.isEmpty()) throw new IllegalArgumentException("Role is required");

        // Create a new Worker object
        Worker worker = new Worker();
        worker.setName(name);
        worker.setEmail(email);
        worker.setPhone(phone);
        worker.setRole(role);
        worker.setSalary(salary);
        worker.setStatus(status);


        // Handle the image file if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            worker.setImage(imageUrl);
        } else {
            worker.setImage(null);
        }

        // Save the worker
        return workerService.saveWorkers(worker);
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

        return "uploads/" + fileName;
    }

    @PutMapping("/{id}")
    public Worker updateWorker(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("role") String role,
            @RequestParam("salary") double salary,
            @RequestParam("status") String status,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        Worker worker = new Worker();
        worker.setId(id);
        worker.setName(name);
        worker.setEmail(email);
        worker.setPhone(phone);
        worker.setRole(role);
        worker.setSalary(salary);
        worker.setStatus(status);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            worker.setImage(imageUrl);
        }

        return workerService.updateWorker(id, worker);
    }

    @DeleteMapping("/{id}")
    public void deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
    }
}
