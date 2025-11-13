package com.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new student.
 */
public class NewStudentRequest {
    
    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

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

    @Override
    public String toString() {
        return "NewStudentRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
