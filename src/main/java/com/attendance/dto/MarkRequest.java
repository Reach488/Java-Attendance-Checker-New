package com.attendance.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request DTO for marking student attendance.
 */
public class MarkRequest {
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Status is required")
    private String status; // "PRESENT" or "ABSENT"
    
    private LocalDate date;

    /**
     * Default constructor.
     */
    public MarkRequest() {
        this.date = LocalDate.now();
    }

    /**
     * Constructor with all fields.
     * @param studentId the student ID
     * @param status the attendance status
     * @param date the date
     */
    public MarkRequest(Long studentId, String status, LocalDate date) {
        this.studentId = studentId;
        this.status = status;
        this.date = date != null ? date : LocalDate.now();
    }

    // Getters and Setters
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MarkRequest{" +
                "studentId=" + studentId +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}
