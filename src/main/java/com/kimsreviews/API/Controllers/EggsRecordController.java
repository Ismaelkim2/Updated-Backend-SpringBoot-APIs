package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.EggsRecordService;
import com.kimsreviews.API.models.EggsRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eggs")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://brishkimecoeggs.onrender.com")
public class EggsRecordController {
    @Autowired
    private EggsRecordService service;

    @GetMapping
    public List<EggsRecord> getAllRecords() {
        return service.getAllRecords();
    }

    @PostMapping
    public EggsRecord addRecord(@RequestBody EggsRecord record) {
        return service.addRecord(record);
    }

    @PutMapping("/{id}")
    public EggsRecord updateRecord(@PathVariable Long id, @RequestBody EggsRecord record) {
        return service.updateRecord(id, record);
    }

    @DeleteMapping("/{id}")
    public void deleteRecord(@PathVariable Long id) {
        service.deleteRecord(id);
    }
}
