package com.attendance.storage;

import com.attendance.model.Student;
import com.attendance.model.Student.AttendanceStatus;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CSV-based storage for daily attendance records.
 * Each day's attendance is stored in a separate CSV file named with the date.
 * File format: student_id,student_name,attendance_status
 */
@Component
public class CsvAttendanceStorage {
    
    private static final String ATTENDANCE_DIR = "attendance_data";
    private static final String FILE_PREFIX = "attendance_";
    private static final String FILE_EXTENSION = ".csv";
    private static final String CSV_HEADER = "student_id,student_name,attendance_status";
    private static final String CSV_DELIMITER = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Constructor - creates attendance directory if it doesn't exist.
     */
    public CsvAttendanceStorage() {
        try {
            Path attendanceDir = Paths.get(ATTENDANCE_DIR);
            if (!Files.exists(attendanceDir)) {
                Files.createDirectories(attendanceDir);
                System.out.println("Created attendance directory: " + ATTENDANCE_DIR);
            }
        } catch (IOException e) {
            System.err.println("Error creating attendance directory: " + e.getMessage());
        }
    }
    
    /**
     * Get the CSV file path for a specific date.
     * @param date the date for the attendance file
     * @return the file path
     */
    private String getFilePath(LocalDate date) {
        String dateStr = date.format(DATE_FORMATTER);
        return ATTENDANCE_DIR + File.separator + FILE_PREFIX + dateStr + FILE_EXTENSION;
    }
    
    /**
     * Save attendance records for a specific date.
     * Creates a new file if it doesn't exist, or updates existing records.
     * @param date the date for the attendance
     * @param students list of students with attendance status
     * @throws IOException if file operations fail
     */
    public void saveAttendance(LocalDate date, List<Student> students) throws IOException {
        String filePath = getFilePath(date);
        File file = new File(filePath);
        
        // Create file if it doesn't exist
        if (!file.exists()) {
            file.createNewFile();
            System.out.println("Created new attendance file: " + filePath);
        }
        
        // Read existing records into a map
        Map<Long, Student> existingRecords = new HashMap<>();
        if (file.length() > 0) {
            existingRecords = readAttendanceAsMap(date);
        }
        
        // Update with new student data
        for (Student student : students) {
            existingRecords.put(student.getId(), student);
        }
        
        // Write all records to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write(CSV_HEADER);
            writer.newLine();
            
            // Write student records
            for (Student student : existingRecords.values()) {
                String line = formatStudentRecord(student);
                writer.write(line);
                writer.newLine();
            }
            
            writer.flush();
            System.out.println("Saved " + existingRecords.size() + " attendance records to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            throw e;
        }
    }
    
    /**
     * Save or update a single student's attendance record for a specific date.
     * @param date the date for the attendance
     * @param student the student with attendance status
     * @throws IOException if file operations fail
     */
    public void saveStudentAttendance(LocalDate date, Student student) throws IOException {
        List<Student> students = new ArrayList<>();
        students.add(student);
        saveAttendance(date, students);
    }
    
    /**
     * Read attendance records for a specific date.
     * @param date the date to read attendance for
     * @return list of students with attendance status
     * @throws IOException if file reading fails
     */
    public List<Student> readAttendance(LocalDate date) throws IOException {
        String filePath = getFilePath(date);
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("No attendance file found for date: " + date);
            return new ArrayList<>();
        }
        
        List<Student> students = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    Student student = parseStudentRecord(line, date);
                    students.add(student);
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
            
            System.out.println("Read " + students.size() + " attendance records from: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            throw e;
        }
        
        return students;
    }
    
    /**
     * Read attendance records as a map (student ID -> Student).
     * @param date the date to read attendance for
     * @return map of student records
     * @throws IOException if file reading fails
     */
    private Map<Long, Student> readAttendanceAsMap(LocalDate date) throws IOException {
        List<Student> students = readAttendance(date);
        Map<Long, Student> studentMap = new HashMap<>();
        for (Student student : students) {
            studentMap.put(student.getId(), student);
        }
        return studentMap;
    }
    
    /**
     * Check if attendance file exists for a specific date.
     * @param date the date to check
     * @return true if file exists, false otherwise
     */
    public boolean attendanceExists(LocalDate date) {
        String filePath = getFilePath(date);
        return new File(filePath).exists();
    }
    
    /**
     * Get list of all dates that have attendance records.
     * @return list of dates with attendance files
     */
    public List<LocalDate> getAvailableDates() {
        List<LocalDate> dates = new ArrayList<>();
        File dir = new File(ATTENDANCE_DIR);
        
        if (!dir.exists() || !dir.isDirectory()) {
            return dates;
        }
        
        File[] files = dir.listFiles((d, name) -> 
            name.startsWith(FILE_PREFIX) && name.endsWith(FILE_EXTENSION));
        
        if (files != null) {
            for (File file : files) {
                try {
                    String fileName = file.getName();
                    // Extract date from filename: attendance_2025-11-13.csv
                    String dateStr = fileName.substring(FILE_PREFIX.length(), 
                                                       fileName.length() - FILE_EXTENSION.length());
                    LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                    dates.add(date);
                } catch (Exception e) {
                    System.err.println("Error parsing date from filename: " + file.getName());
                }
            }
        }
        
        // Sort dates in descending order (most recent first)
        dates.sort(Comparator.reverseOrder());
        return dates;
    }
    
    /**
     * Format a student record as CSV line.
     * @param student the student to format
     * @return CSV formatted string
     */
    private String formatStudentRecord(Student student) {
        return String.format("%d%s%s%s%s",
                student.getId(),
                CSV_DELIMITER,
                escapeCSV(student.getName()),
                CSV_DELIMITER,
                student.getStatus().name());
    }
    
    /**
     * Parse a CSV line into a Student object.
     * @param line the CSV line to parse
     * @param date the date for the attendance record
     * @return Student object
     */
    private Student parseStudentRecord(String line, LocalDate date) {
        String[] parts = line.split(CSV_DELIMITER, -1);
        
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid CSV format: expected 3 columns, got " + parts.length);
        }
        
        Student student = new Student();
        
        try {
            student.setId(Long.parseLong(parts[0].trim()));
            student.setName(unescapeCSV(parts[1].trim()));
            student.setStatus(AttendanceStatus.valueOf(parts[2].trim().toUpperCase()));
            student.setDate(date);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid student ID: " + parts[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid attendance status: " + parts[2]);
        }
        
        return student;
    }
    
    /**
     * Escape special characters in CSV fields.
     * Wraps field in quotes if it contains comma or quote.
     * @param value the value to escape
     * @return escaped value
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            // Escape quotes by doubling them and wrap in quotes
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
    
    /**
     * Unescape CSV field by removing quotes and unescaping doubled quotes.
     * @param value the value to unescape
     * @return unescaped value
     */
    private String unescapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.startsWith("\"") && value.endsWith("\"")) {
            // Remove surrounding quotes and unescape doubled quotes
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        
        return value;
    }
    
    /**
     * Display attendance records for a specific date to console.
     * @param date the date to display attendance for
     */
    public void displayAttendance(LocalDate date) {
        try {
            List<Student> students = readAttendance(date);
            
            if (students.isEmpty()) {
                System.out.println("\nNo attendance records found for: " + date);
                return;
            }
            
            System.out.println("\n========================================");
            System.out.println("   Attendance Report for " + date);
            System.out.println("========================================");
            System.out.printf("%-10s %-30s %-15s%n", "ID", "Name", "Status");
            System.out.println("----------------------------------------");
            
            int presentCount = 0;
            int absentCount = 0;
            
            for (Student student : students) {
                System.out.printf("%-10d %-30s %-15s%n",
                        student.getId(),
                        student.getName(),
                        student.getStatus());
                
                if (student.getStatus() == AttendanceStatus.PRESENT) {
                    presentCount++;
                } else {
                    absentCount++;
                }
            }
            
            System.out.println("========================================");
            System.out.printf("Total Students: %d | Present: %d | Absent: %d%n",
                    students.size(), presentCount, absentCount);
            System.out.printf("Attendance Rate: %.2f%%%n",
                    (presentCount * 100.0 / students.size()));
            System.out.println("========================================\n");
            
        } catch (IOException e) {
            System.err.println("Error displaying attendance: " + e.getMessage());
        }
    }
    
    /**
     * Delete attendance file for a specific date.
     * @param date the date to delete attendance for
     * @return true if file was deleted, false otherwise
     */
    public boolean deleteAttendance(LocalDate date) {
        String filePath = getFilePath(date);
        File file = new File(filePath);
        
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Deleted attendance file: " + filePath);
            }
            return deleted;
        }
        
        return false;
    }
}
