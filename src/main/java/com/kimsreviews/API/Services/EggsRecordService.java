package com.kimsreviews.API.Services;

import com.kimsreviews.API.Repository.EggsRecordRepository;
import com.kimsreviews.API.models.EggsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EggsRecordService {
    @Autowired
    private EggsRecordRepository eggsRecordRepository;

    public List<EggsRecord> getAllRecords() {
        return eggsRecordRepository.findAll();
    }

    public void addRecord(EggsRecord record) {
        eggsRecordRepository.save(record);
    }

    public EggsRecord updateRecord(Long id, EggsRecord record) {
        EggsRecord existingRecord = eggsRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        existingRecord.setDate(record.getDate());
        existingRecord.setEggsCount(record.getEggsCount());
        return eggsRecordRepository.save(existingRecord);
    }

    public void deleteRecord(Long id) {
        if (!eggsRecordRepository.existsById(id)) {
            throw new RuntimeException("Record not found");
        }
        eggsRecordRepository.deleteById(id);
    }
}
