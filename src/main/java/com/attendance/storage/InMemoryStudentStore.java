package com.attendance.storage;

import com.attendance.model.Student;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory storage for Student entities.
 * Thread-safe implementation using ConcurrentHashMap and AtomicLong.
 */
@Component
public class InMemoryStudentStore {
    private final Map<Long, Student> students = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Save a student to the store.
     * If student has no ID, generates a new one.
     * @param student the student to save
     * @return the saved student with ID
     */
    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(idCounter.getAndIncrement());
        }
        students.put(student.getId(), student);
        return student;
    }

    /**
     * Find a student by ID.
     * @param id the student ID
     * @return Optional containing the student if found
     */
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    /**
     * Get all students.
     * @return list of all students
     */
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    /**
     * Search students by name (case-insensitive partial match).
     * @param name the name to search for
     * @return list of matching students
     */
    public List<Student> searchByName(String name) {
        String searchTerm = name.toLowerCase();
        return students.values().stream()
                .filter(student -> student.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Delete a student by ID.
     * @param id the student ID
     */
    public void deleteById(Long id) {
        students.remove(id);
    }

    /**
     * Check if a student exists by ID.
     * @param id the student ID
     * @return true if student exists
     */
    public boolean existsById(Long id) {
        return students.containsKey(id);
    }

    /**
     * Get the count of all students.
     * @return number of students
     */
    public long count() {
        return students.size();
    }

    /**
     * Delete all students.
     */
    public void deleteAll() {
        students.clear();
    }
}
