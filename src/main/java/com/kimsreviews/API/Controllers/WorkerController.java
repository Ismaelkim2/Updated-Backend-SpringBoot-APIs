package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.WorkerService;
import com.kimsreviews.API.models.Worker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "http://localhost:4200")
public class WorkerController {

    private final WorkerService workerService;

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
            @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {

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

        // Call the service to save the worker with the image URL
        return workerService.saveWorkers(worker, image);
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
            @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {

        Worker worker = new Worker();
        worker.setId(id);
        worker.setName(name);
        worker.setEmail(email);
        worker.setPhone(phone);
        worker.setRole(role);
        worker.setSalary(salary);
        worker.setStatus(status);

        // Call the service to update the worker with the new image URL
        return workerService.updateWorker(id, worker, image);
    }

    @DeleteMapping("/{id}")
    public void deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
    }
}
