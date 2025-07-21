Write-Host "=== 测试MySQL数据库连接 ===" -ForegroundColor Green

# 尝试使用telnet连接到MySQL端口
Write-Host "`n1. 测试MySQL端口连通性..." -ForegroundColor Yellow
try {
    $tcpClient = New-Object System.Net.Sockets.TcpClient
    $tcpClient.Connect("localhost", 3306)
    if ($tcpClient.Connected) {
        Write-Host "成功! MySQL端口(3306)可以连接" -ForegroundColor Green
        $tcpClient.Close()
    }
} catch {
    Write-Host "失败: 无法连接到MySQL端口(3306)" -ForegroundColor Red
    Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请确保MySQL服务已启动" -ForegroundColor Yellow
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green

Write-Host "`n=== 检查端口占用情况 ===" -ForegroundColor Green

# 检查8080端口是否被占用
Write-Host "`n1. 检查8080端口..." -ForegroundColor Yellow
try {
    $netstat = netstat -ano | findstr ":8080"
    if ($netstat) {
        Write-Host "8080端口已被占用:" -ForegroundColor Red
        Write-Host $netstat
        
        # 尝试获取进程信息
        $processId = ($netstat -split '\s+')[-1]
        $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
        if ($process) {
            Write-Host "占用进程: $($process.ProcessName) (PID: $processId)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "8080端口未被占用，可以使用" -ForegroundColor Green
    }
} catch {
    Write-Host "检查端口占用失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 检查完成 ===" -ForegroundColor Green

Write-Host "`n=== 解决方案 ===" -ForegroundColor Green
Write-Host "1. 确保MySQL服务已启动" -ForegroundColor Yellow
Write-Host "2. 确保数据库'lucky_mall'已创建" -ForegroundColor Yellow
Write-Host "3. 确保用户名'root'和密码'123456'正确" -ForegroundColor Yellow
Write-Host "4. 如果8080端口被占用，请关闭占用进程或修改后端配置使用其他端口" -ForegroundColor Yellow
Write-Host "5. 使用start_project.bat脚本启动项目" -ForegroundColor Yellow 