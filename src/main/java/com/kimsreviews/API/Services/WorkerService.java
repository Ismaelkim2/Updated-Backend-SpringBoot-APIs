package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.WorkerRepo;
import com.kimsreviews.API.models.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class WorkerService {


    private final WorkerRepo workerRepo;
    private final ImageUploadService imageUploadService;

    public WorkerService(WorkerRepo workerRepo,ImageUploadService imageUploadService) {

        this.workerRepo = workerRepo;
        this.imageUploadService=imageUploadService;
    }

    public List<Worker> getAllWorkers(){
        return workerRepo.findAll();
    }

    public Worker saveWorkers(Worker worker, MultipartFile image) throws Exception {
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(image).toString();
            worker.setImage(imageUrl);
        }
        return workerRepo.save(worker);
    }

    public Worker updateWorker(Long id, Worker worker, MultipartFile image) throws Exception {
        worker.setId(id);
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(image).toString();
            worker.setImage(imageUrl);
        }
        return workerRepo.save(worker);
    }


    public void deleteWorker(Long id) {
        workerRepo.deleteById(id);
    }
}
