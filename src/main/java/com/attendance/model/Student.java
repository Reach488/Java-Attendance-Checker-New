package com.attendance.model;

import java.time.LocalDate;

/**
 * Student entity representing a student in the attendance system.
 * Stores basic student information and current attendance status.
 */
public class Student {
    private Long id;
    private String name;
    private AttendanceStatus status;
    private LocalDate date;

    /**
     * Enumeration for student attendance status.
     */
    public enum AttendanceStatus {
        PRESENT,
        ABSENT
    }

    /**
     * Default constructor.
     */
    public Student() {
        this.status = AttendanceStatus.ABSENT;
        this.date = LocalDate.now();
    }

    /**
     * Constructor with parameters.
     * @param id the student ID
     * @param name the student name
     * @param status the attendance status
     * @param date the date of attendance
     */
    public Student(Long id, String name, AttendanceStatus status, LocalDate date) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.date = date;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
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
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", date=" + date +
                '}';
    }
}
