package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.Services.EggsRecordService;
import com.kimsreviews.API.models.EggsRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // Check if record is valid
            if (record == null || !isValid(record)) {
                throw new IllegalArgumentException("Invalid record data");
            }

            EggsRecord updatedRecord = eggsRecordService.updateRecord(id, record);
            return ResponseEntity.ok(updatedRecord);
        } catch (RuntimeException ex) {
            // Log the error for better visibility
            System.out.println("Error updating record: " + ex.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    private boolean isValid(EggsRecord record) {
        // Implement necessary validation checks, e.g., non-null fields
        return record != null && record.getEggsCount() != null;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRecord(@PathVariable Long id) {
        try {
            eggsRecordService.deleteRecord(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Record deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
