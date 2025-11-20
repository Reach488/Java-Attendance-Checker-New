package com.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a single student's attendance status for a day.
 */
public class AttendanceEntryDTO {
    
    @NotNull
    private Long studentId;
    
    @NotBlank
    private String status;
    
    public AttendanceEntryDTO() {
    }
    
    public AttendanceEntryDTO(Long studentId, String status) {
        this.studentId = studentId;
        this.status = status;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

