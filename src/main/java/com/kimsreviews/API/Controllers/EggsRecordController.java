package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.EggsRecordService;
import com.kimsreviews.API.models.EggsRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eggs")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://brishkimecoeggs.onrender.com")
public class EggsRecordController {
    private final EggsRecordService eggsRecordService;

    @GetMapping
    public ResponseEntity<List<EggsRecord>> getAllRecords() {
        List<EggsRecord> records = eggsRecordService.getAllRecords();
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<?> addRecord(@RequestBody EggsRecord eggsRecord) {
        // Validate the eggsCount and date fields
        if (eggsRecord.getEggsCount() == null || eggsRecord.getDate() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Eggs count and date are required"));
        }

        System.out.println("Received Record: " + eggsRecord);

        // Call the service to save the record
        eggsRecordService.addRecord(eggsRecord);

        // Return a structured JSON response
        SuccessResponse response = new SuccessResponse("Record added successfully");
        return ResponseEntity.ok(response);
    }

    // Success response structure
    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // Error response structure
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EggsRecord> updateRecord(@PathVariable Long id, @RequestBody EggsRecord record) {
        try {
            EggsRecord updatedRecord = eggsRecordService.updateRecord(id, record);
            return ResponseEntity.ok(updatedRecord);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id) {
        try {
            eggsRecordService.deleteRecord(id);
            return ResponseEntity.ok("Record deleted successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
