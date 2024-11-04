package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.RecordService;
import com.kimsreviews.API.models.BirdRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@CrossOrigin(origins = "http://localhost:4200")
public class RecordController {
    @Autowired
    private RecordService recordService;

 @GetMapping("/birds")
   public List<BirdRecord> getBirdRecords() {
        return recordService.getAllBirdRecords();
   }

    @PostMapping("/birds")
    public BirdRecord createBirdRecord(@RequestBody BirdRecord birdRecord) {
        return recordService.saveBirdRecord(birdRecord);
    }

    @PutMapping("/birds/{id}")
    public BirdRecord updateBirdRecord(@PathVariable Long id, @RequestBody BirdRecord birdRecord) {
        return recordService.updateBirdRecord(id, birdRecord);
    }

    @DeleteMapping("/birds/{id}")
    public void deleteBirdRecord(@PathVariable Long id) {
        recordService.deleteBirdRecord(id);
    }

    @GetMapping("/total-birds")
    public Long getTotalBirds() {
        return recordService.getTotalBirds();
    }
}
