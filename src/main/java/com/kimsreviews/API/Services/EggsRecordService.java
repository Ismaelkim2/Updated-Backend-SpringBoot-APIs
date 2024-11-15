package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.EggsRecordRepository;
import com.kimsreviews.API.models.EggsRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EggsRecordService {
    private final EggsRecordRepository repository;

    public List<EggsRecord> getAllRecords() {
        return repository.findAll();
    }

    public EggsRecord getRecordById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
    }

    public EggsRecord saveRecord(EggsRecord record) {
        return repository.save(record);
    }

    public void deleteRecord(Long id) {
        repository.deleteById(id);
    }
}
