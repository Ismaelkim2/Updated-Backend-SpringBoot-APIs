package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.EggsRecordService;
import com.kimsreviews.API.models.EggsRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eggs")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://brishkimecoeggs.onrender.com")
public class EggsRecordController {
    private final EggsRecordService service;

    @GetMapping("/records")
    public List<EggsRecord> getAllRecords() {
        return service.getAllRecords();
    }

    @GetMapping("/records/{id}")
    public EggsRecord getRecordById(@PathVariable Long id) {
        return service.getRecordById(id);
    }

    @PostMapping("/records")
    public EggsRecord createRecord(@RequestBody EggsRecord record) {
        return service.saveRecord(record);
    }

    @PutMapping("/records/{id}")
    public EggsRecord updateRecord(@PathVariable Long id, @RequestBody EggsRecord record) {
        record.setId(id);
        return service.saveRecord(record);
    }

    @DeleteMapping("/records/{id}")
    public void deleteRecord(@PathVariable Long id) {
        service.deleteRecord(id);
    }
}
