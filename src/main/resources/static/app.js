/**
 * Student Attendance Tracker - Frontend JavaScript
 * Enhanced with table format, auto-save, and CSV export
 */

const API_BASE = '/api';
let currentDate = new Date();
let allStudents = [];

// DOM Elements
const addStudentForm = document.getElementById('addStudentForm');
const searchBox = document.getElementById('searchBox');
const messageDiv = document.getElementById('message');
const attendanceTableBody = document.getElementById('attendanceTableBody');
const attendanceDateInput = document.getElementById('attendanceDate');
const currentDateDisplay = document.getElementById('currentDateDisplay');

// Initialize app on load
document.addEventListener('DOMContentLoaded', () => {
    initializeDate();
    loadAllStudents();
    loadAttendanceReport();
    setupEventListeners();
});

/**
 * Initialize date picker
 */
function initializeDate() {
    // Set date input to today
    const today = new Date();
    attendanceDateInput.value = formatDateForInput(today);
    updateDateDisplay();
    
    // Listen for date changes
    attendanceDateInput.addEventListener('change', () => {
        currentDate = new Date(attendanceDateInput.value + 'T00:00:00');
        updateDateDisplay();
        loadAllStudents();
    });
}

/**
 * Update current date display
 */
function updateDateDisplay() {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    currentDateDisplay.textContent = currentDate.toLocaleDateString('en-US', options);
}

/**
 * Set date to today
 */
function setToday() {
    currentDate = new Date();
    attendanceDateInput.value = formatDateForInput(currentDate);
    updateDateDisplay();
    loadAllStudents();
}

/**
 * Go to previous day
 */
function previousDay() {
    currentDate.setDate(currentDate.getDate() - 1);
    attendanceDateInput.value = formatDateForInput(currentDate);
    updateDateDisplay();
    loadAllStudents();
}

/**
 * Go to next day
 */
function nextDay() {
    currentDate.setDate(currentDate.getDate() + 1);
    attendanceDateInput.value = formatDateForInput(currentDate);
    updateDateDisplay();
    loadAllStudents();
}

/**
 * Format date for input field (YYYY-MM-DD)
 */
function formatDateForInput(date) {
    return date.toISOString().split('T')[0];
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    addStudentForm.addEventListener('submit', handleAddStudent);
    searchBox.addEventListener('input', handleSearch);
}

/**
 * Load all students from API
 */
async function loadAllStudents() {
    try {
        const response = await fetch(`${API_BASE}/students`);
        allStudents = await response.json();
        displayAttendanceTable(allStudents);
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
 * Display attendance table with inline edit capability
 */
function displayAttendanceTable(students) {
    if (!students || students.length === 0) {
        attendanceTableBody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px;">No students found. Add students to get started!</td></tr>';
        return;
    }
    
    attendanceTableBody.innerHTML = students.map(student => `
        <tr data-student-id="${student.id}">
            <td class="student-row-id">${student.id}</td>
            <td class="student-row-name">${student.name}</td>
            <td class="status-cell">
                <span class="status-badge ${student.status.toLowerCase()}">
                    ${student.status}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-quick present ${student.status === 'PRESENT' ? 'active' : ''}" 
                            onclick="markAttendanceQuick(${student.id}, 'PRESENT', event)">
                        Present
                    </button>
                    <button class="btn-quick absent ${student.status === 'ABSENT' ? 'active' : ''}" 
                            onclick="markAttendanceQuick(${student.id}, 'ABSENT', event)">
                        Absent
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

/**
 * Quick mark attendance for a student with auto-save
 */
async function markAttendanceQuick(studentId, status, event) {
    // Prevent double-clicking
    if (event) {
        const button = event.target;
        if (button.disabled) return;
        button.disabled = true;
    }
    
    try {
        const response = await fetch(`${API_BASE}/attendance/mark`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                studentId: studentId,
                status: status,
                date: formatDateForInput(currentDate)
            })
        });
        
        if (response.ok) {
            const student = await response.json();
            
            // Update the row in the table immediately
            const row = document.querySelector(`tr[data-student-id="${studentId}"]`);
            if (row) {
                const statusCell = row.querySelector('.status-cell');
                const presentBtn = row.querySelector('.btn-quick.present');
                const absentBtn = row.querySelector('.btn-quick.absent');
                
                // Update status badge
                statusCell.innerHTML = `
                    <span class="status-badge ${status.toLowerCase()}">
                        ${status}
                    </span>
                `;
                
                // Update button states
                presentBtn.classList.remove('active');
                absentBtn.classList.remove('active');
                
                if (status === 'PRESENT') {
                    presentBtn.classList.add('active');
                } else {
                    absentBtn.classList.add('active');
                }
            }
            
            // Update the student in allStudents array
            const studentIndex = allStudents.findIndex(s => s.id === studentId);
            if (studentIndex !== -1) {
                allStudents[studentIndex].status = status;
            }
            
            showMessage(`${student.name} marked as ${status} (Auto-saved to CSV)`, 'success');
            loadAttendanceReport();
        } else {
            const error = await response.json();
            showMessage(error.message || 'Failed to mark attendance', 'error');
        }
    } catch (error) {
        showMessage('Failed to mark attendance', 'error');
        console.error('Error:', error);
    } finally {
        // Re-enable button
        if (event) {
            setTimeout(() => {
                event.target.disabled = false;
            }, 500);
        }
    }
}

/**
 * Mark all students as present
 */
async function markAllPresent() {
    if (!confirm(`Mark all ${allStudents.length} students as PRESENT for ${currentDateDisplay.textContent}?`)) {
        return;
    }
    
    for (const student of allStudents) {
        await markAttendanceQuick(student.id, 'PRESENT');
    }
    
    showMessage(`All students marked as PRESENT (Auto-saved to CSV)`, 'success');
    loadAllStudents();
}

/**
 * Mark all students as absent
 */
async function markAllAbsent() {
    if (!confirm(`Mark all ${allStudents.length} students as ABSENT for ${currentDateDisplay.textContent}?`)) {
        return;
    }
    
    for (const student of allStudents) {
        await markAttendanceQuick(student.id, 'ABSENT');
    }
    
    showMessage(`All students marked as ABSENT (Auto-saved to CSV)`, 'success');
    loadAllStudents();
}

/**
 * Export current day's attendance as CSV file
 */
async function exportCSV() {
    try {
        // Get current date string for filename
        const dateStr = formatDateForInput(currentDate);
        
        // Prepare CSV data
        const csvRows = ['Student ID,Student Name,Attendance Status'];
        
        // Add each student's data
        allStudents.forEach(student => {
            const row = `${student.id},${student.name},${student.status}`;
            csvRows.push(row);
        });
        
        const csvContent = csvRows.join('\n');
        
        // Create blob and download
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        
        link.setAttribute('href', url);
        link.setAttribute('download', `attendance_${dateStr}.csv`);
        link.style.visibility = 'hidden';
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        showMessage(`Exported attendance_${dateStr}.csv`, 'success');
    } catch (error) {
        showMessage('Failed to export CSV', 'error');
        console.error('Export error:', error);
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
 * Handle search input for table filtering
 */
async function handleSearch(e) {
    const searchTerm = e.target.value.trim().toLowerCase();
    
    if (!searchTerm) {
        displayAttendanceTable(allStudents);
        return;
    }
    
    const filtered = allStudents.filter(student => 
        student.name.toLowerCase().includes(searchTerm) ||
        student.id.toString().includes(searchTerm)
    );
    
    displayAttendanceTable(filtered);
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
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}
