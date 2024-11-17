package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.EggsRecordRepository;
import com.kimsreviews.API.models.EggsRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EggsRecordService {
    @Autowired
    private EggsRecordRepository repository;

    public List<EggsRecord> getAllRecords() {
        return repository.findAll();
    }

    public EggsRecord addRecord(EggsRecord record) {
        return repository.save(record);
    }

    public EggsRecord updateRecord(Long id, EggsRecord record) {
        EggsRecord existingRecord = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        existingRecord.setDate(record.getDate());
        existingRecord.setEggCount(record.getEggCount());
        return repository.save(existingRecord);
    }

    public void deleteRecord(Long id) {
        repository.deleteById(id);
    }
}
