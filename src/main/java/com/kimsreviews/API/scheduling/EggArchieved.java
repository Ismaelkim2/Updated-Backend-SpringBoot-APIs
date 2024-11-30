package com.kimsreviews.API.scheduling;

import com.kimsreviews.API.Services.EggsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class EggArchieved {

    @Autowired
    private EggsRecordService eggsRecordService;

    // Run every Sunday at midnight
    @Scheduled(cron = "0 0 0 * * SUN")
    public void archiveWeeklyRecords() {
        eggsRecordService.archiveCurrentWeekData();
    }

}
