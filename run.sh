#!/bin/bash
echo "正在以Mac環境啟動Calendar System..."
cd backend
mvn spring-boot:run -Dspring.profiles.active=mac 