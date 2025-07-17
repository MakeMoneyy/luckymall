@echo off
echo ========================================
echo 启动招财商城项目
echo ========================================
echo.
echo 正在启动后端服务...
start cmd /k "cd backend && mvn spring-boot:run"

echo.
echo 等待3秒，然后启动前端...
timeout /t 3 /nobreak >nul

echo 正在启动前端服务...
start cmd /k "cd frontend && npm start"

echo.
echo ========================================
echo 项目启动完成！
echo ========================================
echo.
echo 后端地址: http://localhost:8080
echo 前端地址: http://localhost:3000
echo.
echo 按任意键关闭此窗口...
pause >nul 