package com.attendance.service;

import com.attendance.dto.AttendanceReportDTO;
import com.attendance.dto.MarkRequest;
import com.attendance.dto.NewStudentRequest;
import com.attendance.dto.StudentDTO;

import java.util.List;

/**
 * Service interface for attendance management operations.
 */
public interface AttendanceService {
    
    /**
     * Add a new student to the system.
     * @param request the new student request
     * @return the created student DTO
     */
    StudentDTO addStudent(NewStudentRequest request);
    
    /**
     * Mark attendance for a student.
     * @param request the mark attendance request
     * @return the updated student DTO
     */
    StudentDTO markAttendance(MarkRequest request);
    
    /**
     * Get all students in the system.
     * @return list of all student DTOs
     */
    List<StudentDTO> getAllStudents();
    
    /**
     * Search for students by name.
     * @param name the name to search for
     * @return list of matching student DTOs
     */
    List<StudentDTO> searchStudent(String name);
    
    /**
     * Get attendance report with statistics.
     * @return attendance report DTO
     */
    AttendanceReportDTO getAttendanceReport();
}
