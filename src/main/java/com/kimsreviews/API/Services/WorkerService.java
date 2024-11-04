package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.WorkerRepo;
import com.kimsreviews.API.models.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerService {


    private final WorkerRepo workerRepo;

    public WorkerService(WorkerRepo workerRepo) {
        this.workerRepo = workerRepo;
    }

    public List<Worker> getAllWorkers(){
        return workerRepo.findAll();
    }

    public Worker saveWorkers (Worker worker){
        return workerRepo.save(worker);
    }

    public Worker updateWorker (Long id,Worker worker){
        worker.setId(id);
        return workerRepo.save(worker);
    }


    public void deleteWorker(Long id) {
        workerRepo.deleteById(id);
    }
}
