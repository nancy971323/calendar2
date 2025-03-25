@echo off
chcp 65001
echo [INFO] Starting frontend application...
cd frontend
call npm install
call npm run serve 