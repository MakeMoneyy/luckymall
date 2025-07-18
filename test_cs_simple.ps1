# 简化版客服API测试脚本

Write-Host "=== 客服API测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"

Write-Host "等待服务启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 测试用户信用卡信息
Write-Host "1. 测试用户信用卡信息..." -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/user/credit-card-info/1" -Method GET
    Write-Host "成功获取用户信用卡信息" -ForegroundColor Green
    Write-Host "卡等级: $($response.data.cardLevel)" -ForegroundColor White
    Write-Host "积分余额: $($response.data.pointsBalance)" -ForegroundColor White
} catch {
    Write-Host "获取用户信用卡信息失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 测试聊天功能
Write-Host "2. 测试聊天功能..." -ForegroundColor Cyan
$chatData = @{
    userId = 1
    sessionId = "test_session_001"
    message = "这个商品可以分期吗？"
    context = @{
        productId = 1
        productPrice = 4999.00
        currentPage = "product_detail"
    }
}

$json = $chatData | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $json -ContentType "application/json"
    Write-Host "聊天功能测试成功" -ForegroundColor Green
    Write-Host "响应时间: $($response.data.responseTimeMs)ms" -ForegroundColor White
    Write-Host "缓存命中: $($response.data.cacheHit)" -ForegroundColor White
} catch {
    Write-Host "聊天功能测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "测试完成！" -ForegroundColor Green 