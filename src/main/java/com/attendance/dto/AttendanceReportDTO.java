package com.attendance.dto;

import java.util.List;

/**
 * DTO for attendance report containing summary statistics.
 */
public class AttendanceReportDTO {
    private long totalStudents;
    private long presentCount;
    private long absentCount;
    private double attendanceRate;
    private List<StudentDTO> students;

    /**
     * Default constructor.
     */
    public AttendanceReportDTO() {
    }

    /**
     * Constructor with all fields.
     * @param totalStudents total number of students
     * @param presentCount number of present students
     * @param absentCount number of absent students
     * @param attendanceRate attendance percentage
     * @param students list of student DTOs
     */
    public AttendanceReportDTO(long totalStudents, long presentCount, long absentCount, 
                              double attendanceRate, List<StudentDTO> students) {
        this.totalStudents = totalStudents;
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.attendanceRate = attendanceRate;
        this.students = students;
    }

    // Getters and Setters
    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(long presentCount) {
        this.presentCount = presentCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }
}
