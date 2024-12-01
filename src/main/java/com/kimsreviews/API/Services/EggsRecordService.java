package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.WeeklyEggRecord;
import com.kimsreviews.API.Repository.EggsRecordRepository;
import com.kimsreviews.API.models.EggsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EggsRecordService {

    @Autowired
    private EggsRecordRepository eggsRecordRepository;


    public List<EggsRecord> getAllRecords() {
        return eggsRecordRepository.findAll(Sort.by(Sort.Direction.ASC, "date"));
    }


    public void addRecord(EggsRecord record) {
        if (record.getEggsCount() == null || record.getDate() == null) {
            throw new IllegalArgumentException("Eggs count and date are required.");
        }
        record.setArchived(false);
        eggsRecordRepository.save(record);
    }

    public List<EggsRecord> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return eggsRecordRepository.findByDateRange(startDate, endDate);
    }



    public EggsRecord updateRecord(Long id, EggsRecord updatedRecord) {
        EggsRecord existingRecord = eggsRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        existingRecord.setEggsCount(updatedRecord.getEggsCount());
        existingRecord.setDate(updatedRecord.getDate());
        return eggsRecordRepository.save(existingRecord);
    }


    public void deleteRecord(Long id) {
        // Check if the record exists before attempting to delete
        if (!eggsRecordRepository.existsById(id)) {
            throw new RuntimeException("Record not found with id: " + id);
        }
        eggsRecordRepository.deleteById(id);
    }


    public List<WeeklyEggRecord> getPreviousRecords() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(WeekFields.ISO.getFirstDayOfWeek());

        List<EggsRecord> records = eggsRecordRepository.findByDateBefore(startOfWeek);

        // Group by week and aggregate eggsCount and brokenEggsCount
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getDate().with(WeekFields.ISO.getFirstDayOfWeek())
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    LocalDate weekStart = entry.getKey();
                    LocalDate weekEnd = weekStart.plusDays(6);

                    int totalEggs = entry.getValue().stream().mapToInt(EggsRecord::getEggsCount).sum();
                    int brokenEggs = 0; // Placeholder, assuming broken eggs data is not stored in EggsRecord

                    return new WeeklyEggRecord(weekStart, weekEnd, totalEggs, brokenEggs);
                })
                .sorted((w1, w2) -> w2.getStartOfWeek().compareTo(w1.getStartOfWeek())) // Sort by week start descending
                .collect(Collectors.toList());
    }

    public EggsRecord getCurrentWeekData() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate endOfWeek = startOfWeek.plus(6, ChronoUnit.DAYS);

        List<EggsRecord> currentWeekRecords = eggsRecordRepository.findAll().stream()
                .filter(record -> !record.getDate().isBefore(startOfWeek) && !record.getDate().isAfter(endOfWeek))
                .toList();

        int totalEggsCount = currentWeekRecords.stream()
                .mapToInt(EggsRecord::getEggsCount)
                .sum();

        EggsRecord summary = new EggsRecord();
        summary.setDate(startOfWeek);
        summary.setEggsCount(totalEggsCount);
        summary.setArchived(false);

        return summary;
    }

    public void archiveCurrentWeekData() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate endOfWeek = startOfWeek.plus(6, ChronoUnit.DAYS);

        // Archive only if today is the end of the week
        if (!today.equals(endOfWeek)) {
            return; // Exit if it's not the end of the week
        }

        List<EggsRecord> currentWeekRecords = eggsRecordRepository.findAll().stream()
                .filter(record -> !record.getDate().isBefore(startOfWeek) && !record.getDate().isAfter(endOfWeek))
                .toList();

        currentWeekRecords.forEach(record -> record.setArchived(true));
        eggsRecordRepository.saveAll(currentWeekRecords);

        // Create and save weekly summary
        int totalEggs = currentWeekRecords.stream().mapToInt(EggsRecord::getEggsCount).sum();
        EggsRecord summaryRecord = new EggsRecord();
        summaryRecord.setDate(startOfWeek);
        summaryRecord.setEggsCount(totalEggs);
        summaryRecord.setArchived(true);
        eggsRecordRepository.save(summaryRecord);
    }

}

