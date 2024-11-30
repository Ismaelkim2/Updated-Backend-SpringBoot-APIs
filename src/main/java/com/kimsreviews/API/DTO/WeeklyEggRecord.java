package com.kimsreviews.API.DTO;

import java.time.LocalDate;

public class WeeklyEggRecord {
    private LocalDate startOfWeek;
    private LocalDate endOfWeek;
    private int eggsCount;
    private int brokenEggsCount;

    public WeeklyEggRecord(LocalDate startOfWeek, LocalDate endOfWeek, int eggsCount, int brokenEggsCount) {
        this.startOfWeek = startOfWeek;
        this.endOfWeek = endOfWeek;
        this.eggsCount = eggsCount;
        this.brokenEggsCount = brokenEggsCount;
    }

    public LocalDate getStartOfWeek() {
        return startOfWeek;
    }

    public LocalDate getEndOfWeek() {
        return endOfWeek;
    }

    public int getEggsCount() {
        return eggsCount;
    }

    public int getBrokenEggsCount() {
        return brokenEggsCount;
    }
}
