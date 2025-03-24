@echo off
echo 正在以Windows環境啟動Calendar System...
cd backend
mvn spring-boot:run -Dspring.profiles.active=windows 