/**
 * Student Attendance Tracker - Frontend JavaScript
 * Enhanced with table format, auto-save, and CSV export
 */

const API_BASE = '/api';
let currentDate = new Date();
let allStudents = [];
let hasPendingChanges = false;

// DOM Elements
const addStudentForm = document.getElementById('addStudentForm');
const searchBox = document.getElementById('searchBox');
const messageDiv = document.getElementById('message');
const attendanceTableBody = document.getElementById('attendanceTableBody');
const attendanceDateInput = document.getElementById('attendanceDate');
const currentDateDisplay = document.getElementById('currentDateDisplay');
const finishButton = document.getElementById('finishButton');

// Initialize app on load
document.addEventListener('DOMContentLoaded', () => {
    initializeDate();
    loadAllStudents();
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
        const dateStr = formatDateForInput(currentDate);
        const response = await fetch(`${API_BASE}/attendance/daily?date=${encodeURIComponent(dateStr)}`);
        allStudents = await response.json();
        refreshTable();
        updateSummaryFromData();
        setPendingChanges(false);
    } catch (error) {
        showMessage('Failed to load students', 'error');
        console.error('Error:', error);
    }
}

function updateSummaryFromData() {
    const total = allStudents.length;
    const present = allStudents.filter(student => student.status === 'PRESENT').length;
    const absent = allStudents.filter(student => student.status === 'ABSENT').length;
    const rate = total > 0 ? ((present * 100) / total).toFixed(1) : '0.0';
    
    document.getElementById('totalStudents').textContent = total;
    document.getElementById('presentCount').textContent = present;
    document.getElementById('absentCount').textContent = absent;
    document.getElementById('attendanceRate').textContent = `${rate}%`;
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
                ${student.status ? `
                    <span class="status-badge ${student.status.toLowerCase()}">
                        ${student.status}
                    </span>` : '<span class="status-badge pending">UNMARKED</span>'}
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn-quick present ${student.status === 'PRESENT' ? 'active' : ''}" 
                            onclick="setAttendanceStatus(${student.id}, 'PRESENT')">
                        Present
                    </button>
                    <button class="btn-quick absent ${student.status === 'ABSENT' ? 'active' : ''}" 
                            onclick="setAttendanceStatus(${student.id}, 'ABSENT')">
                        Absent
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function refreshTable() {
    const searchTerm = searchBox.value.trim().toLowerCase();
    
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
 * Set attendance status locally for a student.
 */
function setAttendanceStatus(studentId, status) {
    const studentIndex = allStudents.findIndex(student => student.id === studentId);
    if (studentIndex === -1) {
        return;
    }
    
    allStudents[studentIndex].status = status;
    refreshTable();
    updateSummaryFromData();
    setPendingChanges(true);
}

/**
 * Mark all students as present
 */
async function markAllPresent() {
    if (!confirm(`Mark all ${allStudents.length} students as PRESENT for ${currentDateDisplay.textContent}?`)) {
        return;
    }
    
    allStudents = allStudents.map(student => ({
        ...student,
        status: 'PRESENT'
    }));
    
    refreshTable();
    updateSummaryFromData();
    setPendingChanges(true);
    showMessage(`All students marked as PRESENT`, 'success');
}

/**
 * Mark all students as absent
 */
async function markAllAbsent() {
    if (!confirm(`Mark all ${allStudents.length} students as ABSENT for ${currentDateDisplay.textContent}?`)) {
        return;
    }
    
    allStudents = allStudents.map(student => ({
        ...student,
        status: 'ABSENT'
    }));
    
    refreshTable();
    updateSummaryFromData();
    setPendingChanges(true);
    showMessage(`All students marked as ABSENT`, 'success');
}

/**
 * Export current day's attendance as CSV file
 */
async function exportCSV() {
    try {
        // Get current date string for filename
        const dateStr = formatDateForInput(currentDate);
        
        // Prepare CSV data
        const csvRows = ['Student ID,Student Name,Attendance Status,Attendance Date'];
        
        // Add each student's data
        allStudents.forEach(student => {
            const row = `${student.id},${student.name},${student.status || ''},${student.date || dateStr}`;
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

async function finishAttendance() {
    if (!hasPendingChanges) {
        showMessage('No changes to save', 'info');
        return;
    }
    
    const hasUnmarked = allStudents.some(student => !student.status);
    if (hasUnmarked) {
        showMessage('Please mark all students before finishing.', 'error');
        return;
    }
    
    const payload = {
        date: formatDateForInput(currentDate),
        entries: allStudents.map(student => ({
            studentId: student.id,
            status: student.status
        }))
    };
    
    try {
        const response = await fetch(`${API_BASE}/attendance/save`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });
        
        if (response.ok) {
            showMessage(`Attendance saved for ${currentDateDisplay.textContent}`, 'success');
            setPendingChanges(false);
            loadAllStudents();
        } else {
            const error = await response.json();
            showMessage(error.message || 'Failed to save attendance', 'error');
        }
    } catch (error) {
        showMessage('Failed to save attendance', 'error');
        console.error('Save error:', error);
    }
}

function setPendingChanges(state) {
    hasPendingChanges = state;
    if (finishButton) {
        finishButton.disabled = !state;
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
    refreshTable();
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
