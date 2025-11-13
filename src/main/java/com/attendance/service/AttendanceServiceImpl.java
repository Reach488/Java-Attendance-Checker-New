package com.attendance.service;

import com.attendance.dto.AttendanceReportDTO;
import com.attendance.dto.MarkRequest;
import com.attendance.dto.NewStudentRequest;
import com.attendance.dto.StudentDTO;
import com.attendance.exception.NotFoundException;
import com.attendance.model.Student;
import com.attendance.model.Student.AttendanceStatus;
import com.attendance.storage.InMemoryStudentStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AttendanceService.
 * Handles business logic for student attendance management.
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {
    
    private final InMemoryStudentStore studentStore;

    /**
     * Constructor with dependency injection.
     * @param studentStore the student storage component
     */
    public AttendanceServiceImpl(InMemoryStudentStore studentStore) {
        this.studentStore = studentStore;
    }

    @Override
    public StudentDTO addStudent(NewStudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setStatus(AttendanceStatus.ABSENT);
        student.setDate(LocalDate.now());
        
        Student saved = studentStore.save(student);
        return StudentDTO.fromEntity(saved);
    }

    @Override
    public StudentDTO markAttendance(MarkRequest request) {
        Student student = studentStore.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException("Student not found with ID: " + request.getStudentId()));
        
        // Update attendance status
        try {
            AttendanceStatus status = AttendanceStatus.valueOf(request.getStatus().toUpperCase());
            student.setStatus(status);
            student.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Must be PRESENT or ABSENT");
        }
        
        Student updated = studentStore.save(student);
        return StudentDTO.fromEntity(updated);
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentStore.findAll().stream()
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> searchStudent(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentStore.searchByName(name).stream()
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceReportDTO getAttendanceReport() {
        List<Student> allStudents = studentStore.findAll();
        long total = allStudents.size();
        long present = allStudents.stream()
                .filter(s -> s.getStatus() == AttendanceStatus.PRESENT)
                .count();
        long absent = total - present;
        double rate = total > 0 ? (present * 100.0 / total) : 0.0;
        
        List<StudentDTO> studentDTOs = allStudents.stream()
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
        
        return new AttendanceReportDTO(total, present, absent, rate, studentDTOs);
    }
}
