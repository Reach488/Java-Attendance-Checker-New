package com.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Request DTO for creating a new student.
 */
public class NewStudentRequest {
    
    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid name without numbers or symbols.")
    private String name;
    
    // NEW: Optional creation date - if not provided, will use current date
    private LocalDate creationDate;

    /**
     * Default constructor.
     */
    public NewStudentRequest() {
    }

    /**
     * Constructor with name parameter.
     * @param name the student name
     */
    public NewStudentRequest(String name) {
        this.name = name;
    }

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // NEW: Getter and Setter for creation date
    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "NewStudentRequest{" +
                "name='" + name + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
