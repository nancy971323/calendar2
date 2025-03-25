@echo off
echo [INFO] Starting frontend application...
cd frontend
call npm install
call npm run serve
pause 