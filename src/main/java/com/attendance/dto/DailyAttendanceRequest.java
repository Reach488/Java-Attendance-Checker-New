package com.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Request body for saving a day's attendance in bulk.
 */
public class DailyAttendanceRequest {
    
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @NotEmpty
    @Valid
    private List<AttendanceEntryDTO> entries;
    
    public DailyAttendanceRequest() {
    }
    
    public DailyAttendanceRequest(LocalDate date, List<AttendanceEntryDTO> entries) {
        this.date = date;
        this.entries = entries;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public List<AttendanceEntryDTO> getEntries() {
        return entries;
    }
    
    public void setEntries(List<AttendanceEntryDTO> entries) {
        this.entries = entries;
    }
}

