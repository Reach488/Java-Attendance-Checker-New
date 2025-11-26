package com.attendance.service;

import com.attendance.dto.AttendanceEntryDTO;
import com.attendance.dto.AttendanceReportDTO;
import com.attendance.dto.DailyAttendanceRequest;
import com.attendance.dto.NewStudentRequest;
import com.attendance.dto.StudentDTO;
import com.attendance.exception.NotFoundException;
import com.attendance.model.Student;
import com.attendance.model.Student.AttendanceStatus;
import com.attendance.storage.InMemoryStudentStore;
import com.attendance.storage.CsvAttendanceStorage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AttendanceService.
 * Handles business logic for student attendance management with CSV persistence.
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {
    
    private final InMemoryStudentStore studentStore;
    private final CsvAttendanceStorage csvStorage;

    /**
     * Constructor with dependency injection.
     * @param studentStore the student storage component
     * @param csvStorage the CSV storage component
     */
    public AttendanceServiceImpl(InMemoryStudentStore studentStore, CsvAttendanceStorage csvStorage) {
        this.studentStore = studentStore;
        this.csvStorage = csvStorage;
        // Load today's attendance from CSV if it exists
        loadTodayAttendance();
    }
    
    /**
     * Load attendance data from CSV for today's date.
     */
    private void loadTodayAttendance() {
        try {
            LocalDate today = LocalDate.now();
            if (csvStorage.attendanceExists(today)) {
                List<Student> students = csvStorage.readAttendance(today);
                for (Student student : students) {
                    studentStore.save(student);
                }
                System.out.println("Loaded " + students.size() + " students from today's attendance file");
            }
        } catch (IOException e) {
            System.err.println("Error loading today's attendance: " + e.getMessage());
        }
    }

    @Override
    public StudentDTO addStudent(NewStudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setStatus(AttendanceStatus.ABSENT);
        
        // UPDATED: Use creation date from request, or default to today
        LocalDate creationDate = request.getCreationDate() != null 
                ? request.getCreationDate() 
                : LocalDate.now();
        student.setCreationDate(creationDate);
        student.setDate(creationDate); // Set attendance date to creation date initially
        
        Student saved = studentStore.save(student);
        return StudentDTO.fromEntity(saved);
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentStore.findAll().stream()
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StudentDTO> getAttendanceForDate(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        
        // UPDATED: Filter students by creation date - only include students created on or before target date
        List<Student> baseStudents = studentStore.findAll().stream()
                .filter(student -> {
                    LocalDate creationDate = student.getCreationDate() != null 
                            ? student.getCreationDate() 
                            : student.getDate() != null ? student.getDate() : LocalDate.now();
                    return !creationDate.isAfter(targetDate); // creationDate <= targetDate
                })
                .collect(Collectors.toList());
        
        Map<Long, Student> attendanceForDate = new HashMap<>();
        
        if (csvStorage.attendanceExists(targetDate)) {
            try {
                List<Student> attendanceStudents = csvStorage.readAttendance(targetDate);
                for (Student record : attendanceStudents) {
                    // NEW: Also filter CSV records by creation date
                    LocalDate recordCreationDate = record.getCreationDate() != null 
                            ? record.getCreationDate() 
                            : record.getDate() != null ? record.getDate() : targetDate;
                    if (!recordCreationDate.isAfter(targetDate)) {
                        attendanceForDate.put(record.getId(), record);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading attendance for " + targetDate + ": " + e.getMessage());
            }
        }
        
        List<StudentDTO> result = baseStudents.stream()
                .map(baseStudent -> {
                    Student record = attendanceForDate.get(baseStudent.getId());
                    String status = record != null && record.getStatus() != null
                            ? record.getStatus().name()
                            : null;
                    LocalDate recordDate = record != null ? record.getDate() : targetDate;
                    return new StudentDTO(
                            baseStudent.getId(),
                            baseStudent.getName(),
                            status,
                            recordDate
                    );
                })
                .collect(Collectors.toList());
        
        // UPDATED: Only add CSV records that don't exist in result and pass creation date filter
        attendanceForDate.forEach((id, record) -> {
            boolean exists = result.stream().anyMatch(dto -> dto.getId().equals(id));
            if (!exists) {
                LocalDate recordCreationDate = record.getCreationDate() != null 
                        ? record.getCreationDate() 
                        : record.getDate() != null ? record.getDate() : targetDate;
                if (!recordCreationDate.isAfter(targetDate)) {
                    result.add(StudentDTO.fromEntity(record));
                }
            }
        });
        
        result.sort(Comparator.comparing(StudentDTO::getId));
        return result;
    }
    
    @Override
    public void saveDailyAttendance(DailyAttendanceRequest request) {
        LocalDate targetDate = request.getDate() != null ? request.getDate() : LocalDate.now();
        Map<Long, Student> roster = studentStore.findAll().stream()
                .collect(Collectors.toMap(Student::getId, student -> student));
        
        List<Student> recordsToPersist = new ArrayList<>();
        
        for (AttendanceEntryDTO entry : request.getEntries()) {
            Student base = roster.get(entry.getStudentId());
            if (base == null) {
                throw new NotFoundException("Student not found with ID: " + entry.getStudentId());
            }
            
            AttendanceStatus status;
            try {
                status = AttendanceStatus.valueOf(entry.getStatus().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid status for student " + entry.getStudentId());
            }
            
            Student record = new Student();
            record.setId(base.getId());
            record.setName(base.getName());
            record.setStatus(status);
            record.setDate(targetDate);
            // UPDATED: Preserve creation date from base student
            record.setCreationDate(base.getCreationDate() != null 
                    ? base.getCreationDate() 
                    : base.getDate() != null ? base.getDate() : targetDate);
            recordsToPersist.add(record);
            
            // keep in-memory store in sync with latest saved status
            studentStore.save(record);
        }
        
        try {
            csvStorage.writeDailyAttendance(targetDate, recordsToPersist);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save attendance for " + targetDate, e);
        }
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
    public AttendanceReportDTO getAttendanceReport(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<StudentDTO> dailyAttendance = getAttendanceForDate(targetDate);
        
        long total = dailyAttendance.size();
        long present = dailyAttendance.stream()
                .filter(s -> "PRESENT".equalsIgnoreCase(s.getStatus()))
                .count();
        long absent = dailyAttendance.stream()
                .filter(s -> "ABSENT".equalsIgnoreCase(s.getStatus()))
                .count();
        double rate = total > 0 ? (present * 100.0 / total) : 0.0;
        
        return new AttendanceReportDTO(total, present, absent, rate, dailyAttendance);
    }
}
