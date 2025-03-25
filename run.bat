@echo off
chcp 65001
echo [INFO] Starting Calendar System in Windows environment...
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=windows" 