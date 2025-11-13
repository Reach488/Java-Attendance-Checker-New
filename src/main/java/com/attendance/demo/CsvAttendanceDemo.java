package com.attendance.demo;

import com.attendance.model.Student;
import com.attendance.model.Student.AttendanceStatus;
import com.attendance.storage.CsvAttendanceStorage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Standalone Demo Application for CSV-based Student Attendance Tracker.
 * This demonstrates all CSV file operations without requiring Spring Boot to run.
 * 
 * Features:
 * - Create new CSV attendance file for current date
 * - Mark attendance for multiple students
 * - Update existing attendance records
 * - Read and display attendance from any date
 * - List all available attendance dates
 * - Proper exception handling for all file operations
 */
public class CsvAttendanceDemo {
    
    private static final CsvAttendanceStorage csvStorage = new CsvAttendanceStorage();
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Student Attendance Tracker (CSV)");
        System.out.println("========================================\n");
        
        boolean running = true;
        
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            System.out.println();
            
            switch (choice) {
                case 1 -> markTodayAttendance();
                case 2 -> viewAttendanceByDate();
                case 3 -> viewAllAvailableDates();
                case 4 -> markAttendanceForCustomDate();
                case 5 -> addMultipleStudents();
                case 6 -> {
                    System.out.println("Thank you for using Student Attendance Tracker!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.\n");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Display the main menu.
     */
    private static void displayMenu() {
        System.out.println("========================================");
        System.out.println("MAIN MENU");
        System.out.println("========================================");
        System.out.println("1. Mark Attendance for Today");
        System.out.println("2. View Attendance by Date");
        System.out.println("3. View All Available Dates");
        System.out.println("4. Mark Attendance for Custom Date");
        System.out.println("5. Add Multiple Students");
        System.out.println("6. Exit");
        System.out.println("========================================");
    }
    
    /**
     * Mark attendance for today's date.
     */
    private static void markTodayAttendance() {
        LocalDate today = LocalDate.now();
        System.out.println("Marking Attendance for: " + today);
        System.out.println("----------------------------------------");
        
        try {
            // Check if attendance file already exists
            if (csvStorage.attendanceExists(today)) {
                System.out.println("Attendance file already exists. You can add or update records.");
                List<Student> existing = csvStorage.readAttendance(today);
                System.out.println("Current records: " + existing.size() + " students\n");
            } else {
                System.out.println("Creating new attendance file for today...\n");
            }
            
            // Get student details
            long studentId = getIntInput("Enter Student ID: ");
            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();
            
            // Get attendance status
            System.out.println("Select Attendance Status:");
            System.out.println("1. Present");
            System.out.println("2. Absent");
            int statusChoice = getIntInput("Enter choice: ");
            
            AttendanceStatus status = (statusChoice == 1) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
            
            // Create student record
            Student student = new Student();
            student.setId(studentId);
            student.setName(name);
            student.setStatus(status);
            student.setDate(today);
            
            // Save to CSV
            csvStorage.saveStudentAttendance(today, student);
            
            System.out.println("\n✓ Attendance marked successfully!");
            System.out.println("  Student: " + name + " (" + studentId + ")");
            System.out.println("  Status: " + status);
            System.out.println("  Date: " + today + "\n");
            
            // Ask if user wants to mark more students
            System.out.print("Mark attendance for another student? (y/n): ");
            String more = scanner.nextLine();
            if (more.equalsIgnoreCase("y")) {
                markTodayAttendance();
            }
            
        } catch (IOException e) {
            System.err.println("✗ Error marking attendance: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * View attendance records for a specific date.
     */
    private static void viewAttendanceByDate() {
        System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
        String dateStr = scanner.nextLine().trim();
        
        LocalDate date;
        if (dateStr.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.err.println("✗ Invalid date format. Use YYYY-MM-DD format.\n");
                return;
            }
        }
        
        // Display attendance
        csvStorage.displayAttendance(date);
    }
    
    /**
     * View all available attendance dates.
     */
    private static void viewAllAvailableDates() {
        List<LocalDate> dates = csvStorage.getAvailableDates();
        
        if (dates.isEmpty()) {
            System.out.println("No attendance records found.\n");
            return;
        }
        
        System.out.println("========================================");
        System.out.println("  Available Attendance Dates");
        System.out.println("========================================");
        for (int i = 0; i < dates.size(); i++) {
            System.out.printf("%d. %s%n", (i + 1), dates.get(i));
        }
        System.out.println("========================================");
        System.out.println("Total: " + dates.size() + " date(s)\n");
    }
    
    /**
     * Mark attendance for a custom date.
     */
    private static void markAttendanceForCustomDate() {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine().trim();
        
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.err.println("✗ Invalid date format. Use YYYY-MM-DD format.\n");
            return;
        }
        
        System.out.println("Marking Attendance for: " + date);
        System.out.println("----------------------------------------");
        
        try {
            // Get student details
            long studentId = getIntInput("Enter Student ID: ");
            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();
            
            // Get attendance status
            System.out.println("Select Attendance Status:");
            System.out.println("1. Present");
            System.out.println("2. Absent");
            int statusChoice = getIntInput("Enter choice: ");
            
            AttendanceStatus status = (statusChoice == 1) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
            
            // Create student record
            Student student = new Student();
            student.setId(studentId);
            student.setName(name);
            student.setStatus(status);
            student.setDate(date);
            
            // Save to CSV
            csvStorage.saveStudentAttendance(date, student);
            
            System.out.println("\n✓ Attendance marked successfully!");
            System.out.println("  Student: " + name + " (" + studentId + ")");
            System.out.println("  Status: " + status);
            System.out.println("  Date: " + date + "\n");
            
        } catch (IOException e) {
            System.err.println("✗ Error marking attendance: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Add multiple students at once.
     */
    private static void addMultipleStudents() {
        LocalDate date = LocalDate.now();
        System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
        String dateStr = scanner.nextLine().trim();
        
        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.err.println("✗ Invalid date format. Use YYYY-MM-DD format.\n");
                return;
            }
        }
        
        System.out.println("Adding Multiple Students for: " + date);
        System.out.println("----------------------------------------");
        
        int numStudents = getIntInput("How many students do you want to add? ");
        
        List<Student> students = new ArrayList<>();
        
        for (int i = 1; i <= numStudents; i++) {
            System.out.println("\nStudent " + i + ":");
            long studentId = getIntInput("  Enter Student ID: ");
            System.out.print("  Enter Student Name: ");
            String name = scanner.nextLine();
            
            System.out.println("  Select Attendance Status:");
            System.out.println("    1. Present");
            System.out.println("    2. Absent");
            int statusChoice = getIntInput("  Enter choice: ");
            
            AttendanceStatus status = (statusChoice == 1) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
            
            Student student = new Student();
            student.setId(studentId);
            student.setName(name);
            student.setStatus(status);
            student.setDate(date);
            
            students.add(student);
        }
        
        // Save all students
        try {
            csvStorage.saveAttendance(date, students);
            System.out.println("\n✓ Successfully added " + students.size() + " students!");
            System.out.println("  Date: " + date + "\n");
            
            // Display summary
            csvStorage.displayAttendance(date);
            
        } catch (IOException e) {
            System.err.println("✗ Error saving students: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get integer input from user with error handling.
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
