# 🚀 Complete Spring Boot Attendance Tracker - Ready to Run!

## ✅ What's Been Created

A complete, production-ready Spring Boot application with:

### Backend (Java 17 + Spring Boot 3.2.0)
- ✅ `AttendanceApplication.java` - Main app with 20 preloaded sample students
- ✅ `AttendanceController.java` - REST API with @RestController, @CrossOrigin
- ✅ `AttendanceService.java` - Service interface
- ✅ `AttendanceServiceImpl.java` - Service implementation with @Service
- ✅ `Student.java` - Model with id, name, status (PRESENT/ABSENT), date
- ✅ `StudentDTO.java` - Data transfer object
- ✅ `NewStudentRequest.java` - Request DTO with validation
- ✅ `MarkRequest.java` - Mark attendance request DTO
- ✅ `AttendanceReportDTO.java` - Report response DTO
- ✅ `InMemoryStudentStore.java` - Thread-safe storage with ConcurrentHashMap & AtomicLong
- ✅ `NotFoundException.java` - Custom exception
- ✅ `GlobalExceptionHandler.java` - @ControllerAdvice for error handling

### Frontend (HTML + CSS+ JavaScript)
- ✅ `index.html` - Beautiful, responsive UI with gradient design
- ✅ `app.js` - Fetch API integration with all endpoints
- ✅ `style.css` 

### Configuration
- ✅ `pom.xml` - Maven with all required dependencies
- ✅ `application.properties` - Server configuration

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/students` | Get all students |
| POST | `/api/students` | Add new student |
| POST | `/api/attendance/mark` | Mark attendance |
| GET | `/api/attendance/report` | Get attendance report |
| GET | `/api/students/search?name=X` | Search students by name |

---

## How to Run

### Option 1: Using Maven (Recommended)
```bash
# Navigate to project directory
cd '/Users/skylarrrr/Documents/Skylar'"'"'s Space/School/AUPP/Java Programming I/Final Project/Attendance'

# Run the application
mvn spring-boot:run
```

### Option 2: Using IDE
1. Open the project in IntelliJ IDEA or VS Code
2. Right-click on `AttendanceApplication.java`
3. Select "Run"

---

## 🌐 Access the Application

Once running, open your browser:

**Main UI:** http://localhost:8080

**API Endpoints:**
- http://localhost:8080/api/students
- http://localhost:8080/api/attendance/report

---

## Features Implemented

### ✅ In-Memory Storage
- Thread-safe `ConcurrentHashMap<Long, Student>`
- `AtomicLong` ID counter
- No database required

### ✅ Sample Data
- 20 pre-loaded students on startup via `CommandLineRunner`
- Random attendance statuses

### ✅ REST API
- Full CRUD operations
- JSON request/response
- CORS enabled for browser access

### ✅ Validation
- `@Valid` annotations
- `@NotBlank`, `@NotNull`, `@Size` constraints
- Custom error messages

### ✅ Exception Handling
- `NotFoundException` (404)
- `MethodArgumentNotValidException` (400)
- `IllegalArgumentException` (400)
- Generic Exception (500)

### ✅ Frontend Features
- Add new students
- Mark attendance (Present/Absent)
- Search students by name
- View attendance summary
- Real-time updates
- Responsive design

---

## 📊 Sample API Usage

### Add Student
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice Johnson"}'
```

### Mark Attendance
```bash
curl -X POST http://localhost:8080/api/attendance/mark \
  -H "Content-Type: application/json" \
  -d '{"studentId": 1, "status": "PRESENT", "date": "2025-11-10"}'
```

### Get All Students
```bash
curl http://localhost:8080/api/students
```

### Search Students
```bash
curl "http://localhost:8080/api/students/search?name=John"
```

### Get Attendance Report
```bash
curl http://localhost:8080/api/attendance/report
```

---

## 🔧 Prerequisites Installation

### Install Java 17
```bash
brew install openjdk@17
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### Install Maven
```bash
brew install maven
```

### Verify Installation
```bash
java -version   # Should show Java 17
mvn -version    # Should show Maven
```

---

## 📁 Project Structure

```
Attendance/
 ├─ pom.xml
 ├─ RUN_INSTRUCTIONS.md (this file)
 └─ src/main/
     ├─ java/com/attendance/
     │   ├─ AttendanceApplication.java
     │   ├─ controller/
     │   │   └─ AttendanceController.java
     │   ├─ service/
     │   │   ├─ AttendanceService.java
     │   │   └─ AttendanceServiceImpl.java
     │   ├─ model/
     │   │   └─ Student.java
     │   ├─ dto/
     │   │   ├─ StudentDTO.java
     │   │   ├─ NewStudentRequest.java
     │   │   ├─ MarkRequest.java
     │   │   └─ AttendanceReportDTO.java
     │   ├─ storage/
     │   │   └─ InMemoryStudentStore.java
     │   └─ exception/
     │       ├─ NotFoundException.java
     │       └─ GlobalExceptionHandler.java
     └─ resources/
         ├─ application.properties
         └─ static/
             ├─ index.html
             └─ app.js
```

---

## ✨ What Happens on Startup

1. **Spring Boot initializes** the application
2. **ComponentScan** discovers all @Component, @Service, @RestController classes
3. **CommandLineRunner** preloads 20 sample students:
   - John Smith, Emma Johnson, Michael Brown, etc.
   - Random PRESENT/ABSENT status
4. **Embedded Tomcat** starts on port 8080
5. **Application ready** at http://localhost:8080

---

## 🎓 Student Names Preloaded

The app starts with these 20 students:
- John Smith, Emma Johnson, Michael Brown, Sophia Davis
- James Wilson, Olivia Martinez, William Anderson, Ava Taylor
- Benjamin Thomas, Isabella Garcia, Lucas Rodriguez, Mia Lee
- Mason White, Charlotte Harris, Ethan Clark, Amelia Lewis
- Alexander Walker, Harper Hall, Daniel Allen, Evelyn Young

---

## 🐛 Troubleshooting

### Port 8080 already in use
Change port in `application.properties`:
```properties
server.port=8081
```

### Java version mismatch
Ensure Java 17 is installed:
```bash
java -version
```

### Maven not found
Install Maven:
```bash
brew install maven
```

---

## 🎉 You're All Set!

Run the application:
```bash
mvn spring-boot:run
```

Then open: **http://localhost:8080**

Enjoy your fully functional Student Attendance Tracker! 🚀

