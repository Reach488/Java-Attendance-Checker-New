package com.attendance;

import com.attendance.model.Student;
import com.attendance.model.Student.AttendanceStatus;
import com.attendance.storage.InMemoryStudentStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Random;

/**
 * Main Spring Boot application class for the Attendance System.
 * Preloads sample student data on startup.
 */
@SpringBootApplication
public class AttendanceApplication {

    /**
     * Main method to start the Spring Boot application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
    }

    /**
     * CommandLineRunner bean to preload sample data on startup.
     * Creates 20 sample students with random attendance statuses.
     * @param studentStore the in-memory student store
     * @return CommandLineRunner instance
     */
    @Bean
    public CommandLineRunner preloadData(InMemoryStudentStore studentStore) {
        return args -> {
            System.out.println("========================================");
            System.out.println("📚 Attendance System Starting...");
            System.out.println("========================================");
            
            // Sample student names
            String[] studentNames = {
                "John Smith", "Emma Johnson", "Michael Brown", "Sophia Davis",
                "James Wilson", "Olivia Martinez", "William Anderson", "Ava Taylor",
                "Benjamin Thomas", "Isabella Garcia", "Lucas Rodriguez", "Mia Lee",
                "Mason White", "Charlotte Harris", "Ethan Clark", "Amelia Lewis",
                "Alexander Walker", "Harper Hall", "Daniel Allen", "Evelyn Young"
            };
            
            Random random = new Random();
            AttendanceStatus[] statuses = AttendanceStatus.values();
            
            // Preload 20 sample students
            for (String name : studentNames) {
                Student student = new Student();
                student.setName(name);
                student.setStatus(statuses[random.nextInt(statuses.length)]);
                student.setDate(LocalDate.now());
                studentStore.save(student);
            }
            
            System.out.println("✅ Preloaded " + studentStore.count() + " sample students");
            System.out.println("========================================");
            System.out.println("🚀 Application ready!");
            System.out.println("📍 Access at: http://localhost:8080");
            System.out.println("📡 API Base: http://localhost:8080/api");
            System.out.println("========================================");
        };
    }
}
