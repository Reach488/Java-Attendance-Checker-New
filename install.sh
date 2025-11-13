#!/bin/bash

echo "🚀 Attendance System - Installation Script"
echo "=========================================="
echo ""

# Check if Java is installed
echo "📋 Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "✅ Java is installed: $JAVA_VERSION"
else
    echo "❌ Java is NOT installed"
    echo "   Installing Java 11..."
    brew install openjdk@11
    echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
    source ~/.zshrc
fi

echo ""

# Check if Maven is installed
echo "📋 Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo "✅ Maven is installed: $MVN_VERSION"
else
    echo "❌ Maven is NOT installed"
    echo "   Installing Maven..."
    brew install maven
fi

echo ""
echo "📥 Downloading project dependencies..."
echo "   This may take a few minutes on first run..."
mvn clean install -DskipTests

echo ""
echo "✅ Installation complete!"
echo ""
echo "🎉 You can now run the application with:"
echo "   mvn spring-boot:run"
echo ""
echo "📚 For more information, check SETUP_INSTRUCTIONS.md"

