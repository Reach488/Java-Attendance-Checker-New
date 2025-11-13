package com.attendance.demo;

import com.attendance.model.Student;
import com.attendance.model.Student.AttendanceStatus;
import com.attendance.storage.CsvAttendanceStorage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple test class to demonstrate CSV Attendance Storage functionality.
 * This can be run directly to test all CSV operations.
 */
public class CsvAttendanceTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  CSV Attendance Storage Test");
        System.out.println("========================================\n");
        
        CsvAttendanceStorage csvStorage = new CsvAttendanceStorage();
        LocalDate today = LocalDate.now();
        
        try {
            // Test 1: Create and save attendance records
            testSaveAttendance(csvStorage, today);
            
            // Test 2: Read attendance records
            testReadAttendance(csvStorage, today);
            
            // Test 3: Update existing records
            testUpdateAttendance(csvStorage, today);
            
            // Test 4: Display attendance
            System.out.println("\n--- Test 4: Display Attendance ---");
            csvStorage.displayAttendance(today);
            
            // Test 5: List all available dates
            testListAvailableDates(csvStorage);
            
            System.out.println("\n========================================");
            System.out.println("  All Tests Completed Successfully!");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("\n✗ Test Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test saving attendance records.
     */
    private static void testSaveAttendance(CsvAttendanceStorage csvStorage, LocalDate date) throws IOException {
        System.out.println("--- Test 1: Save Attendance Records ---");
        
        List<Student> students = new ArrayList<>();
        
        // Create sample students
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Alice Johnson");
        student1.setStatus(AttendanceStatus.PRESENT);
        student1.setDate(date);
        students.add(student1);
        
        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Bob Smith");
        student2.setStatus(AttendanceStatus.PRESENT);
        student2.setDate(date);
        students.add(student2);
        
        Student student3 = new Student();
        student3.setId(3L);
        student3.setName("Charlie Davis");
        student3.setStatus(AttendanceStatus.ABSENT);
        student3.setDate(date);
        students.add(student3);
        
        Student student4 = new Student();
        student4.setId(4L);
        student4.setName("Diana Martinez");
        student4.setStatus(AttendanceStatus.PRESENT);
        student4.setDate(date);
        students.add(student4);
        
        Student student5 = new Student();
        student5.setId(5L);
        student5.setName("Eva Wilson");
        student5.setStatus(AttendanceStatus.ABSENT);
        student5.setDate(date);
        students.add(student5);
        
        // Save to CSV
        csvStorage.saveAttendance(date, students);
        
        System.out.println("✓ Saved " + students.size() + " students to CSV");
        System.out.println("  Date: " + date);
        System.out.println("  File: attendance_" + date + ".csv\n");
    }
    
    /**
     * Test reading attendance records.
     */
    private static void testReadAttendance(CsvAttendanceStorage csvStorage, LocalDate date) throws IOException {
        System.out.println("--- Test 2: Read Attendance Records ---");
        
        List<Student> students = csvStorage.readAttendance(date);
        
        System.out.println("✓ Read " + students.size() + " students from CSV");
        System.out.println("\nStudent Details:");
        for (Student student : students) {
            System.out.printf("  ID: %d, Name: %s, Status: %s%n",
                    student.getId(), student.getName(), student.getStatus());
        }
        System.out.println();
    }
    
    /**
     * Test updating existing attendance records.
     */
    private static void testUpdateAttendance(CsvAttendanceStorage csvStorage, LocalDate date) throws IOException {
        System.out.println("--- Test 3: Update Attendance Record ---");
        
        // Update student 3 (Charlie) to PRESENT
        Student updatedStudent = new Student();
        updatedStudent.setId(3L);
        updatedStudent.setName("Charlie Davis");
        updatedStudent.setStatus(AttendanceStatus.PRESENT);
        updatedStudent.setDate(date);
        
        csvStorage.saveStudentAttendance(date, updatedStudent);
        
        System.out.println("✓ Updated student ID 3 (Charlie Davis) to PRESENT");
        
        // Add a new student
        Student newStudent = new Student();
        newStudent.setId(6L);
        newStudent.setName("Frank Anderson");
        newStudent.setStatus(AttendanceStatus.PRESENT);
        newStudent.setDate(date);
        
        csvStorage.saveStudentAttendance(date, newStudent);
        
        System.out.println("✓ Added new student ID 6 (Frank Anderson)\n");
    }
    
    /**
     * Test listing all available dates.
     */
    private static void testListAvailableDates(CsvAttendanceStorage csvStorage) {
        System.out.println("\n--- Test 5: List Available Dates ---");
        
        List<LocalDate> dates = csvStorage.getAvailableDates();
        
        System.out.println("✓ Found " + dates.size() + " attendance file(s)");
        System.out.println("\nAvailable Dates:");
        for (LocalDate date : dates) {
            System.out.println("  - " + date);
        }
    }
}
