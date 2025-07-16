@echo off
echo 招财商城后端服务启动脚本
echo ================================

echo 1. 清理和编译项目...
call mvn clean compile

if %errorlevel% neq 0 (
    echo 编译失败！请检查代码。
    pause
    exit /b 1
)

echo 2. 启动Spring Boot应用...
echo 请确保MySQL数据库已启动并且密码是123456
echo 访问地址: http://localhost:8080
echo 按 Ctrl+C 停止服务

call mvn spring-boot:run

pause 