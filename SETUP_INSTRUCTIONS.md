# �� Attendance System - Setup Instructions

## Required Software

### 1. **Java Development Kit (JDK) 11 or higher**
   - Check if installed: `java -version`
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or use Homebrew: `brew install openjdk@11`

### 2. **Maven (Build Tool)**
   - Check if installed: `mvn -version`
   - Install with Homebrew: `brew install maven`
   - Or download from: https://maven.apache.org/download.cgi

### 3. **IDE (Optional but Recommended)**
   - IntelliJ IDEA Community Edition: https://www.jetbrains.com/idea/download/
   - Eclipse IDE: https://www.eclipse.org/downloads/
   - VS Code with Java Extension Pack

---

## 📥 Installation Steps

### Step 1: Install Java (if not installed)
```bash
# Check Java version
java -version

# Install Java 11 using Homebrew (macOS)
brew install openjdk@11

# Add to PATH
echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### Step 2: Install Maven (if not installed)
```bash
# Check Maven version
mvn -version

# Install Maven using Homebrew (macOS)
brew install maven
```

### Step 3: Download All Dependencies
```bash
# Navigate to project directory
cd '/Users/skylarrrr/Documents/Skylar'"'"'s Space/School/AUPP/Java Programming I/Final Project/Attendance'

# Download all dependencies (this will take a few minutes)
mvn clean install

# Or just download dependencies without building
mvn dependency:resolve
```

---

## 📚 Dependencies Included

The project uses the following packages (automatically downloaded by Maven):

1. **Spring Boot 2.7.18**
   - `spring-boot-starter-web` - Web application framework, REST APIs
   - `spring-boot-starter-validation` - Data validation
   - `spring-boot-devtools` - Hot reload for development
   
2. **Jackson** - JSON processing for Java 8 date/time types

3. **Lombok** (Optional) - Reduces boilerplate code

4. **Spring Boot Test** - Testing framework

---

## 🚀 How to Run the Project

### Option 1: Using Maven
```bash
mvn spring-boot:run
```

### Option 2: Using Java
```bash
# Build the JAR file
mvn clean package

# Run the JAR
java -jar target/attendance-system-1.0.0.jar
```

### Option 3: Using IDE
- Open the project in IntelliJ IDEA or Eclipse
- Right-click on `AttendanceApplication.java`
- Select "Run"

---

## 🌐 Access the Application

Once running, open your browser and go to:
```
http://localhost:8080
```

The static files (index.html, app.js) will be accessible at:
```
http://localhost:8080/index.html
```

---

## 🔧 Verify Installation

Run this command to check everything is working:
```bash
mvn clean compile
```

If successful, you should see "BUILD SUCCESS"

---

## 📝 Common Issues

### Issue: "mvn: command not found"
**Solution**: Install Maven using `brew install maven`

### Issue: "JAVA_HOME not set"
**Solution**: 
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 11)' >> ~/.zshrc
```

### Issue: Port 8080 already in use
**Solution**: Change port in `src/main/resources/application.properties`:
```
server.port=8081
```

---

## 📖 Next Steps

1. Install Java and Maven if not already installed
2. Run `mvn clean install` to download all dependencies
3. Run `mvn spring-boot:run` to start the application
4. Start coding your attendance system! 🎉

