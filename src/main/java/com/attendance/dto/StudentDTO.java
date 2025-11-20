package com.attendance.dto;

import com.attendance.model.Student;
import java.time.LocalDate;

/**
 * Data Transfer Object for Student information.
 * Used to transfer student data between layers.
 */
public class StudentDTO {
    private Long id;
    private String name;
    private String status;
    private LocalDate date;

    /**
     * Default constructor.
     */
    public StudentDTO() {
    }

    /**
     * Constructor with all fields.
     * @param id the student ID
     * @param name the student name
     * @param status the attendance status
     * @param date the attendance date
     */
    public StudentDTO(Long id, String name, String status, LocalDate date) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.date = date;
    }

    /**
     * Factory method to create StudentDTO from Student entity.
     * @param student the student entity
     * @return StudentDTO object
     */
    public static StudentDTO fromEntity(Student student) {
        return new StudentDTO(
            student.getId(),
            student.getName(),
            student.getStatus() != null ? student.getStatus().name() : null,
            student.getDate()
        );
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
}
