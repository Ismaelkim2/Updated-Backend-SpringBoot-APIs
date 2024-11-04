package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.BirdsRecordRepo;
import com.kimsreviews.API.models.BirdRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordService {
    @Autowired
    private BirdsRecordRepo birdsRecordRepo;

    public List<BirdRecord> getAllBirdRecords() {
        return birdsRecordRepo.findAll();
    }

    public BirdRecord saveBirdRecord(BirdRecord birdRecord) {
        return birdsRecordRepo.save(birdRecord);
    }

    public BirdRecord updateBirdRecord(Long id, BirdRecord birdRecord) {
        birdRecord.setId(id);
        return birdsRecordRepo.save(birdRecord);
    }

    public void deleteBirdRecord(Long id) {
        birdsRecordRepo.deleteById(id);
    }

    public Long getTotalBirds() {
        return birdsRecordRepo.count();
    }
}

