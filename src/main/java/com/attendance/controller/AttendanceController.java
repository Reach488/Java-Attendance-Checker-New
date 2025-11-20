package com.attendance.controller;

import com.attendance.dto.AttendanceReportDTO;
import com.attendance.dto.DailyAttendanceRequest;
import com.attendance.dto.NewStudentRequest;
import com.attendance.dto.StudentDTO;
import com.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * REST Controller for attendance management endpoints.
 * Handles HTTP requests for student and attendance operations.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AttendanceController {
    
    private final AttendanceService attendanceService;

    /**
     * Constructor with dependency injection.
     * @param attendanceService the attendance service
     */
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Get all students.
     * @return list of all students
     */
    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = attendanceService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * Add a new student.
     * @param request the new student request
     * @return the created student
     */
    @PostMapping("/students")
    public ResponseEntity<StudentDTO> addStudent(@Valid @RequestBody NewStudentRequest request) {
        StudentDTO student = attendanceService.addStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    /**
     * Mark attendance for a student.
     * @param request the mark attendance request
     * @return the updated student
     */
    @GetMapping("/attendance/daily")
    public ResponseEntity<List<StudentDTO>> getDailyAttendance(@RequestParam String date) {
        try {
            List<StudentDTO> students = attendanceService.getAttendanceForDate(LocalDate.parse(date));
            return ResponseEntity.ok(students);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }
    
    /**
     * Save attendance for all students on a specific date.
     * @param request payload with date and statuses
     * @return success response
     */
    @PostMapping("/attendance/save")
    public ResponseEntity<Void> saveDailyAttendance(@Valid @RequestBody DailyAttendanceRequest request) {
        attendanceService.saveDailyAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Get attendance report with statistics.
     * @return the attendance report
     */
    @GetMapping("/attendance/report")
    public ResponseEntity<AttendanceReportDTO> getAttendanceReport(
            @RequestParam(value = "date", required = false) String date) {
        LocalDate targetDate = null;
        if (date != null && !date.isBlank()) {
            try {
                targetDate = LocalDate.parse(date);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
            }
        }
        
        AttendanceReportDTO report = attendanceService.getAttendanceReport(targetDate);
        return ResponseEntity.ok(report);
    }

    /**
     * Search for students by name.
     * @param name the name to search for
     * @return list of matching students
     */
    @GetMapping("/students/search")
    public ResponseEntity<List<StudentDTO>> searchStudent(@RequestParam String name) {
        List<StudentDTO> students = attendanceService.searchStudent(name);
        return ResponseEntity.ok(students);
    }
}
