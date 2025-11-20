package com.attendance.service;

import com.attendance.dto.AttendanceReportDTO;
import com.attendance.dto.DailyAttendanceRequest;
import com.attendance.dto.NewStudentRequest;
import com.attendance.dto.StudentDTO;

import java.time.LocalDate;
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
     * Get all students in the system.
     * @return list of all student DTOs
     */
    List<StudentDTO> getAllStudents();
    
    /**
     * Get attendance for a specific date.
     * @param date the target date
     * @return list of student DTOs for the date
     */
    List<StudentDTO> getAttendanceForDate(LocalDate date);
    
    /**
     * Persist attendance for a specific date.
     * @param request payload containing the date and student statuses
     */
    void saveDailyAttendance(DailyAttendanceRequest request);
    
    /**
     * Search for students by name.
     * @param name the name to search for
     * @return list of matching student DTOs
     */
    List<StudentDTO> searchStudent(String name);
    
    /**
     * Get attendance report with statistics for a date.
     * @param date the date to report on
     * @return attendance report DTO
     */
    AttendanceReportDTO getAttendanceReport(LocalDate date);
}
