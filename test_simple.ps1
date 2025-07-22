# 简单测试脚本
$baseUrl = "http://localhost:8080"
$userId = 1
$sessionId = "test-" + [DateTime]::Now.Ticks

Write-Host "开始测试..." -ForegroundColor Cyan

# 测试1: 基础对话
Write-Host "1. 基础对话测试..." -ForegroundColor Cyan
$chatRequest = @{
    userId = $userId
    sessionId = $sessionId
    message = "你好，我想问一下退货政策"
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    Write-Host "✓ 基础对话测试成功" -ForegroundColor Green
    Write-Host "  - 响应: $($response.data.response)" -ForegroundColor White
} catch {
    Write-Host "✗ 基础对话测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试2: 缓存性能
Write-Host "2. 缓存性能测试..." -ForegroundColor Cyan
try {
    # 第一次请求
    $startTime = Get-Date
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $firstRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    # 第二次请求（应该命中缓存）
    $startTime = Get-Date
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $secondRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    Write-Host "✓ 缓存性能测试成功" -ForegroundColor Green
    Write-Host "  - 第一次请求时间: $([Math]::Round($firstRequestTime, 2))ms" -ForegroundColor White
    Write-Host "  - 第二次请求时间: $([Math]::Round($secondRequestTime, 2))ms" -ForegroundColor White
    Write-Host "  - 第二次缓存命中: $($response2.data.cacheHit)" -ForegroundColor White
    
    if ($response2.data.cacheHit -and $secondRequestTime -lt $firstRequestTime) {
        Write-Host "  ✓ 缓存机制工作正常，性能提升明显" -ForegroundColor Green
        $improvement = (1 - ($secondRequestTime / $firstRequestTime)) * 100
        Write-Host "  - 性能提升: $([Math]::Round($improvement, 2))%" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ 缓存机制可能未正常工作" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ 缓存性能测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试3: 人工客服转接
Write-Host "3. 人工客服转接测试..." -ForegroundColor Cyan
$transferSessionId = "transfer-" + [DateTime]::Now.Ticks

$transferRequest = @{
    userId = $userId
    sessionId = $transferSessionId
    reason = "需要专业帮助"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/human-service/transfer" -Method POST -Body $transferRequest -ContentType "application/json"
    Write-Host "✓ 人工客服转接请求成功" -ForegroundColor Green
    Write-Host "  - 会话ID: $($response.data.sessionId)" -ForegroundColor White
    Write-Host "  - 状态: $($response.data.status)" -ForegroundColor White
    
    # 保存转接会话ID
    $humanServiceSessionId = $response.data.sessionId
    
    # 查询转接状态
    if ($humanServiceSessionId) {
        $statusResponse = Invoke-RestMethod -Uri "$baseUrl/api/human-service/status?sessionId=$humanServiceSessionId" -Method GET
        Write-Host "  - 队列位置: $($statusResponse.data.queuePosition)" -ForegroundColor White
        Write-Host "  - 预计等待时间: $($statusResponse.data.estimatedWaitTime)分钟" -ForegroundColor White
    }
} catch {
    Write-Host "✗ 人工客服转接测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "测试完成!" -ForegroundColor Cyan 