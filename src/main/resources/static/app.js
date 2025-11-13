/**
 * Student Attendance Tracker - Frontend JavaScript
 * Handles all API calls and DOM updates
 */

const API_BASE = '/api';

// DOM Elements
const studentsTable = document.getElementById('studentsTable');
const addStudentForm = document.getElementById('addStudentForm');
const markAttendanceForm = document.getElementById('markAttendanceForm');
const searchBox = document.getElementById('searchBox');
const messageDiv = document.getElementById('message');

// Initialize app on load
document.addEventListener('DOMContentLoaded', () => {
    loadAllStudents();
    loadAttendanceReport();
    setupEventListeners();
});

/**
 * Setup event listeners
 */
function setupEventListeners() {
    addStudentForm.addEventListener('submit', handleAddStudent);
    markAttendanceForm.addEventListener('submit', handleMarkAttendance);
    searchBox.addEventListener('input', handleSearch);
}

/**
 * Load all students from API
 */
async function loadAllStudents() {
    try {
        const response = await fetch(`${API_BASE}/students`);
        const students = await response.json();
        displayStudents(students);
    } catch (error) {
        showMessage('Failed to load students', 'error');
        console.error('Error:', error);
    }
}

/**
 * Load attendance report and update summary cards
 */
async function loadAttendanceReport() {
    try {
        const response = await fetch(`${API_BASE}/attendance/report`);
        const report = await response.json();
        updateSummaryCards(report);
    } catch (error) {
        console.error('Error loading report:', error);
    }
}

/**
 * Handle add student form submission
 */
async function handleAddStudent(e) {
    e.preventDefault();
    const name = document.getElementById('studentName').value.trim();
    
    if (!name) {
        showMessage('Please enter a student name', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/students`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name })
        });
        
        if (response.ok) {
            const student = await response.json();
            showMessage(`Student "${student.name}" added successfully! ID: ${student.id}`, 'success');
            addStudentForm.reset();
            loadAllStudents();
            loadAttendanceReport();
        } else {
            const error = await response.json();
            showMessage(error.message || 'Failed to add student', 'error');
        }
    } catch (error) {
        showMessage('Failed to add student', 'error');
        console.error('Error:', error);
    }
}

/**
 * Handle mark attendance form submission
 */
async function handleMarkAttendance(e) {
    e.preventDefault();
    const studentId = document.getElementById('markStudentId').value;
    const status = document.getElementById('attendanceStatus').value;
    
    if (!studentId || !status) {
        showMessage('Please fill all fields', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/attendance/mark`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                studentId: parseInt(studentId),
                status: status,
                date: new Date().toISOString().split('T')[0]
            })
        });
        
        if (response.ok) {
            const student = await response.json();
            showMessage(`Attendance marked for "${student.name}" as ${status}`, 'success');
            markAttendanceForm.reset();
            loadAllStudents();
            loadAttendanceReport();
        } else {
            const error = await response.json();
            showMessage(error.message || 'Failed to mark attendance', 'error');
        }
    } catch (error) {
        showMessage('Failed to mark attendance', 'error');
        console.error('Error:', error);
    }
}

/**
 * Handle search input
 */
async function handleSearch(e) {
    const searchTerm = e.target.value.trim();
    
    if (!searchTerm) {
        loadAllStudents();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/students/search?name=${encodeURIComponent(searchTerm)}`);
        const students = await response.json();
        displayStudents(students);
    } catch (error) {
        console.error('Error searching:', error);
    }
}

/**
 * Display students in table
 */
function displayStudents(students) {
    if (!students || students.length === 0) {
        studentsTable.innerHTML = '<tr><td colspan="4" style="text-align: center;">No students found</td></tr>';
        return;
    }
    
    studentsTable.innerHTML = students.map(student => `
        <tr>
            <td>${student.id}</td>
            <td>${student.name}</td>
            <td class="status-${student.status.toLowerCase()}">${student.status}</td>
            <td>${formatDate(student.date)}</td>
        </tr>
    `).join('');
}

/**
 * Update summary cards with report data
 */
function updateSummaryCards(report) {
    document.getElementById('totalStudents').textContent = report.totalStudents;
    document.getElementById('presentCount').textContent = report.presentCount;
    document.getElementById('absentCount').textContent = report.absentCount;
    document.getElementById('attendanceRate').textContent = report.attendanceRate.toFixed(1) + '%';
}

/**
 * Show message to user
 */
function showMessage(text, type = 'success') {
    messageDiv.textContent = text;
    messageDiv.className = `message ${type} show`;
    
    setTimeout(() => {
        messageDiv.classList.remove('show');
    }, 5000);
}

/**
 * Format date for display
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}
