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
    // UPDATED: Added creation_date column to track when student was added
    private static final String CSV_HEADER = "date,student_id,student_name,attendance_status,creation_date";
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
                if (student.getDate() == null) {
                    student.setDate(date);
                }
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
        LocalDate recordDate = student.getDate() != null ? student.getDate() : LocalDate.now();
        // UPDATED: Include creation_date in CSV output
        LocalDate creationDate = student.getCreationDate() != null ? student.getCreationDate() : recordDate;
        return String.format("%s%s%d%s%s%s%s%s%s",
                recordDate.format(DATE_FORMATTER),
                CSV_DELIMITER,
                student.getId(),
                CSV_DELIMITER,
                escapeCSV(student.getName()),
                CSV_DELIMITER,
                student.getStatus().name(),
                CSV_DELIMITER,
                creationDate.format(DATE_FORMATTER));
    }
    
    /**
     * Parse a CSV line into a Student object.
     * @param line the CSV line to parse
     * @param date the date for the attendance record
     * @return Student object
     */
    private Student parseStudentRecord(String line, LocalDate dateFromFileName) {
        String[] parts = line.split(CSV_DELIMITER, -1);
        
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid CSV format: expected at least 3 columns, got " + parts.length);
        }
        
        Student student = new Student();
        
        try {
            // UPDATED: Handle both old format (4 columns) and new format (5 columns with creation_date)
            int offset = (parts.length == 4 || parts.length == 5) ? 1 : 0;
            LocalDate recordDate = (parts.length == 4 || parts.length == 5)
                    ? LocalDate.parse(parts[0].trim(), DATE_FORMATTER)
                    : dateFromFileName;
            
            student.setId(Long.parseLong(parts[offset].trim()));
            student.setName(unescapeCSV(parts[offset + 1].trim()));
            student.setStatus(AttendanceStatus.valueOf(parts[offset + 2].trim().toUpperCase()));
            student.setDate(recordDate);
            
            // NEW: Parse creation_date if present (5 columns), otherwise use recordDate for backward compatibility
            if (parts.length == 5 && !parts[offset + 3].trim().isEmpty()) {
                try {
                    LocalDate creationDate = LocalDate.parse(parts[offset + 3].trim(), DATE_FORMATTER);
                    student.setCreationDate(creationDate);
                } catch (Exception e) {
                    // If parsing fails, fall back to recordDate
                    student.setCreationDate(recordDate);
                }
            } else {
                // Backward compatibility: use recordDate as creationDate for old CSV files
                student.setCreationDate(recordDate);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid student ID: " + parts[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid attendance status: " + parts[parts.length - 1]);
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
    
    /**
     * Remove a student from attendance records for a specific date.
     * @param date the date to remove the student from
     * @param studentId the student ID to remove
     * @throws IOException if file operations fail
     */
    public void removeStudentFromAttendance(LocalDate date, Long studentId) throws IOException {
        String filePath = getFilePath(date);
        File file = new File(filePath);
        
        if (!file.exists()) {
            return; // No file to update
        }
        
        // Read all students except the one to delete
        List<Student> students = readAttendance(date);
        students.removeIf(s -> s.getId().equals(studentId));
        
        // Rewrite the file without the deleted student
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write(CSV_HEADER);
            writer.newLine();
            
            // Write remaining student records
            for (Student student : students) {
                String line = formatStudentRecord(student);
                writer.write(line);
                writer.newLine();
            }
            
            writer.flush();
            System.out.println("Removed student " + studentId + " from attendance file: " + filePath);
        } catch (IOException e) {
            System.err.println("Error removing student from file: " + filePath);
            throw e;
        }
    }
    
    /**
     * Remove a student from all attendance files.
     * @param studentId the student ID to remove
     * @throws IOException if file operations fail
     */
    public void removeStudentFromAllAttendance(Long studentId) throws IOException {
        List<LocalDate> dates = getAvailableDates();
        for (LocalDate date : dates) {
            removeStudentFromAttendance(date, studentId);
        }
    }

    /**
     * Overwrite a day's attendance with the provided records.
     * @param date target date
     * @param students list of students with their statuses
     * @throws IOException if file operations fail
     */
    public void writeDailyAttendance(LocalDate date, List<Student> students) throws IOException {
        String filePath = getFilePath(date);
        File file = new File(filePath);
        
        if (!file.exists()) {
            file.createNewFile();
        }
        
        students.sort(Comparator.comparing(Student::getId));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            
            for (Student student : students) {
                if (student.getDate() == null) {
                    student.setDate(date);
                }
                writer.write(formatStudentRecord(student));
                writer.newLine();
            }
            writer.flush();
        }
    }
}
