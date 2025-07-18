# 测试客服API的PowerShell脚本

Write-Host "=== 招财商城智能客服API测试 ===" -ForegroundColor Green
Write-Host ""

$baseUrl = "http://localhost:8080"

# 等待服务启动
Write-Host "等待后端服务启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 测试1: 获取用户信用卡信息
Write-Host "1. 测试获取用户信用卡信息..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/user/credit-card-info/1" -Method GET
    Write-Host "✓ 用户信用卡信息获取成功" -ForegroundColor Green
    Write-Host "  - 卡等级: $($response.data.cardLevel)" -ForegroundColor White
    Write-Host "  - 积分余额: $($response.data.pointsBalance)" -ForegroundColor White
    Write-Host "  - 即将过期积分: $($response.data.pointsExpiring)" -ForegroundColor White
} catch {
    Write-Host "✗ 用户信用卡信息获取失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 测试2: 分期咨询
Write-Host "2. 测试分期咨询..." -ForegroundColor Cyan
$chatRequest = @{
    userId = 1
    sessionId = "test_session_001"
    message = "这个商品可以分期吗？"
    context = @{
        productId = 1
        productPrice = 4999.00
        currentPage = "product_detail"
    }
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    Write-Host "✓ 分期咨询测试成功" -ForegroundColor Green
    Write-Host "  - 响应消息: $($response.data.message.Substring(0, [Math]::Min(100, $response.data.message.Length)))..." -ForegroundColor White
    Write-Host "  - 缓存命中: $($response.data.cacheHit)" -ForegroundColor White
    Write-Host "  - 响应时间: $($response.data.responseTimeMs)ms" -ForegroundColor White
    Write-Host "  - 建议选项: $($response.data.suggestions -join ', ')" -ForegroundColor White
} catch {
    Write-Host "✗ 分期咨询测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 测试3: 积分咨询
Write-Host "3. 测试积分咨询..." -ForegroundColor Cyan
$chatRequest2 = @{
    userId = 1
    sessionId = "test_session_002"
    message = "我的积分有什么用？"
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest2 -ContentType "application/json"
    Write-Host "✓ 积分咨询测试成功" -ForegroundColor Green
    Write-Host "  - 响应消息: $($response.data.message.Substring(0, [Math]::Min(100, $response.data.message.Length)))..." -ForegroundColor White
    Write-Host "  - 缓存命中: $($response.data.cacheHit)" -ForegroundColor White
    Write-Host "  - 响应时间: $($response.data.responseTimeMs)ms" -ForegroundColor White
} catch {
    Write-Host "✗ 积分咨询测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 测试4: 缓存测试 - 重复相同问题
Write-Host "4. 测试缓存功能（重复相同问题）..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    Write-Host "✓ 缓存测试成功" -ForegroundColor Green
    Write-Host "  - 缓存命中: $($response.data.cacheHit)" -ForegroundColor White
    Write-Host "  - 响应时间: $($response.data.responseTimeMs)ms" -ForegroundColor White
    if ($response.data.cacheHit) {
        Write-Host "  ✓ 缓存正常工作，响应速度更快" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ 缓存未命中，可能需要检查缓存配置" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ 缓存测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 测试5: FAQ测试
Write-Host "5. 测试FAQ功能..." -ForegroundColor Cyan
$chatRequest3 = @{
    userId = 2
    sessionId = "test_session_003"
    message = "订单什么时候发货？"
    context = @{
        currentPage = "order"
    }
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest3 -ContentType "application/json"
    Write-Host "✓ FAQ测试成功" -ForegroundColor Green
    Write-Host "  - 响应消息: $($response.data.message.Substring(0, [Math]::Min(150, $response.data.message.Length)))..." -ForegroundColor White
    Write-Host "  - 建议选项: $($response.data.suggestions -join ', ')" -ForegroundColor White
} catch {
    Write-Host "✗ FAQ测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Green
Write-Host "提示：可以在浏览器中访问 http://localhost:3000 测试前端界面" -ForegroundColor Yellow 