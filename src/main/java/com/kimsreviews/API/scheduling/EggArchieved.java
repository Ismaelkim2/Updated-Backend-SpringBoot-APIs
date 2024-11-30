package com.kimsreviews.API.scheduling;

import com.kimsreviews.API.Services.EggsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component // Ensures Spring detects and manages this class
public class EggArchieved {

    @Autowired
    private EggsRecordService eggsRecordService;

    // Run every Sunday at midnight
    @Scheduled(cron = "0 0 0 ? * SUN") // Correct cron expression
    public void archiveWeeklyRecords() {
        eggsRecordService.archiveCurrentWeekData();
    }
}
