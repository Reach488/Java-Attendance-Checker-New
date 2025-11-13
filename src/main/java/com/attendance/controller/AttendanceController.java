package com.attendance.controller;

import com.attendance.dto.AttendanceReportDTO;
import com.attendance.dto.MarkRequest;
import com.attendance.dto.NewStudentRequest;
import com.attendance.dto.StudentDTO;
import com.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/attendance/mark")
    public ResponseEntity<StudentDTO> markAttendance(@Valid @RequestBody MarkRequest request) {
        StudentDTO student = attendanceService.markAttendance(request);
        return ResponseEntity.ok(student);
    }

    /**
     * Get attendance report with statistics.
     * @return the attendance report
     */
    @GetMapping("/attendance/report")
    public ResponseEntity<AttendanceReportDTO> getAttendanceReport() {
        AttendanceReportDTO report = attendanceService.getAttendanceReport();
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
