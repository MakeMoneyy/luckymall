Write-Host "正在启动招财商城前后端服务..." -ForegroundColor Green

# 设置环境变量
$env:DASHSCOPE_API_KEY="sk-4c300d8d5d1a42a28f71d2e13be2543a"

# 启动后端服务
Write-Host "`n启动后端服务..." -ForegroundColor Yellow
Start-Process -FilePath "powershell" -ArgumentList "-Command", "cd $PSScriptRoot; mvn -f backend/pom.xml spring-boot:run"

# 等待后端启动
Write-Host "等待后端服务启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 启动前端服务
Write-Host "`n启动前端服务..." -ForegroundColor Yellow
Start-Process -FilePath "powershell" -ArgumentList "-Command", "cd $PSScriptRoot/frontend; npm start"

Write-Host "`n服务已启动！" -ForegroundColor Green
Write-Host "后端API: http://localhost:8080" -ForegroundColor Cyan
Write-Host "前端页面: http://localhost:3000" -ForegroundColor Cyan
Write-Host "`n按Ctrl+C可以停止服务" -ForegroundColor Yellow

# 保持脚本运行
while ($true) {
    Start-Sleep -Seconds 5
} 