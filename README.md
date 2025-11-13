# 🎓 Attendance Management System

A modern Java-based attendance tracking system built with Spring Boot.

## 📦 Required Software & Packages

### Prerequisites
- **Java JDK 11+** - Core programming language
- **Maven** - Build tool and dependency manager
- **Spring Boot 2.7.18** - Application framework

### Included Dependencies (Auto-downloaded by Maven)
All these packages will be automatically downloaded when you run Maven:

| Package | Purpose | Size |
|---------|---------|------|
| Spring Boot Starter Web | REST API & Web Server | ~15 MB |
| Spring Boot Starter Validation | Data validation | ~2 MB |
| Spring Boot DevTools | Hot reload in development | ~1 MB |
| Jackson Datatype JSR310 | JSON date/time handling | ~200 KB |
| Lombok | Reduce boilerplate code | ~2 MB |
| Spring Boot Starter Test | Testing framework | ~10 MB |

**Total Download Size:** ~50-70 MB (first time only)

---

## 🚀 Quick Start Guide

### Step 1: Install Required Software

**Option A - Automated (Recommended)**
```bash
./install.sh
```

**Option B - Manual**
```bash
# Install Java
brew install openjdk@11
echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Install Maven
brew install maven

# Download all dependencies
mvn clean install
```

### Step 2: Run the Application
```bash
mvn spring-boot:run
```

### Step 3: Access the Application
Open your browser and go to: `http://localhost:8080`

---

## 📁 Project Structure

```
attendance/
 ├─ pom.xml                          # Maven configuration & dependencies
 ├─ README.md                        # This file
 ├─ SETUP_INSTRUCTIONS.md           # Detailed setup guide
 ├─ install.sh                      # Automated installation script
 └─ src/main/
     ├─ java/com/attendance/
     │   ├─ AttendanceApplication.java      # Main entry point
     │   ├─ controller/
     │   │   └─ AttendanceController.java   # REST API endpoints
     │   ├─ service/
     │   │   ├─ AttendanceService.java      # Service interface
     │   │   └─ AttendanceServiceImpl.java  # Business logic
     │   ├─ model/
     │   │   └─ Student.java                # Student entity
     │   ├─ dto/
     │   │   ├─ StudentDTO.java             # Student response
     │   │   ├─ NewStudentRequest.java      # Add student request
     │   │   ├─ MarkRequest.java            # Mark attendance request
     │   │   └─ AttendanceReportDTO.java    # Report response
     │   ├─ storage/
     │   │   └─ InMemoryStudentStore.java   # Data storage
     │   └─ exception/
     │       ├─ NotFoundException.java      # Custom exception
     │       └─ GlobalExceptionHandler.java # Error handling
     └─ resources/
         ├─ application.properties          # App configuration
         └─ static/
             ├─ index.html                  # Frontend UI
             └─ app.js                      # JavaScript code
```

---

## 🛠️ Available Commands

| Command | Description |
|---------|-------------|
| `mvn clean install` | Download dependencies & build project |
| `mvn spring-boot:run` | Run the application |
| `mvn clean compile` | Compile the code |
| `mvn test` | Run tests |
| `mvn clean package` | Build JAR file |
| `java -jar target/attendance-system-1.0.0.jar` | Run JAR file |

---

## 📚 API Endpoints (To be implemented)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/students` | Get all students |
| GET | `/api/students/{id}` | Get student by ID |
| POST | `/api/students` | Add new student |
| POST | `/api/students/{id}/attendance` | Mark attendance |
| GET | `/api/students/{id}/report` | Get attendance report |
| DELETE | `/api/students/{id}` | Delete student |

---

## 🔧 Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Application Info
app.name=Attendance System
app.version=1.0.0
```

---

## ❓ Troubleshooting

### Issue: "mvn: command not found"
**Solution:** Install Maven
```bash
brew install maven
```

### Issue: "java: command not found"
**Solution:** Install Java
```bash
brew install openjdk@11
```

### Issue: "Port 8080 already in use"
**Solution:** Change port in `application.properties`
```properties
server.port=8081
```

### Issue: Dependencies not downloading
**Solution:** Clear Maven cache and retry
```bash
rm -rf ~/.m2/repository
mvn clean install
```

---

## 📖 What Each Package Does

### Spring Boot Starter Web
- Provides web server (Tomcat)
- REST API support
- JSON processing
- HTTP request handling

### Spring Boot Starter Validation
- Input validation
- Data constraints
- Error messages

### Jackson
- Converts Java objects to JSON
- Handles Java 8 date/time types
- Serialization/deserialization

### Lombok (Optional)
- Auto-generates getters/setters
- Reduces boilerplate code
- Makes code cleaner

### Spring Boot DevTools
- Automatic restart on code changes
- Live reload
- Faster development

---

## 👨‍💻 Development Tips

1. **Install IDE:** Use IntelliJ IDEA or Eclipse for better development experience
2. **Enable Hot Reload:** Spring DevTools is already included
3. **Check Logs:** Application logs appear in the terminal
4. **Test APIs:** Use Postman or curl to test endpoints

---

## 📝 Next Steps

1. ✅ Project structure created
2. ✅ Dependencies configured
3. ⬜ Install Java & Maven (run `./install.sh`)
4. ⬜ Download dependencies (run `mvn clean install`)
5. ⬜ Implement REST API controllers
6. ⬜ Add Spring annotations (@RestController, @Service, etc.)
7. ⬜ Test the application
8. ⬜ Build frontend UI

---

## 📞 Support

For detailed setup instructions, see: **SETUP_INSTRUCTIONS.md**

---

**Happy Coding! 🎉**
