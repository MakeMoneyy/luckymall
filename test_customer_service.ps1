Write-Host "测试智能客服API" -ForegroundColor Green

# 测试参数
$baseUrl = "http://localhost:8080"
$userId = "test-user-" + (Get-Random)
$sessionId = [guid]::NewGuid().ToString()

# 设置编码为UTF-8
$PSDefaultParameterValues['Out-File:Encoding'] = 'utf8'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# 测试聊天API
Write-Host "`n测试聊天API..." -ForegroundColor Yellow
$chatUrl = "$baseUrl/api/chat"
$body = @{
    userId = $userId
    sessionId = $sessionId
    message = "你好，我想了解一下信用卡支付有什么优惠？"
    context = @{}
} | ConvertTo-Json

Write-Host "发送请求: $body"
try {
    $response = Invoke-RestMethod -Uri $chatUrl -Method Post -Body $body -ContentType "application/json"
    Write-Host "响应:" -ForegroundColor Cyan
    $response | ConvertTo-Json
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# 等待一下
Start-Sleep -Seconds 2

# 测试第二次请求（应该命中缓存）
Write-Host "`n测试缓存命中..." -ForegroundColor Yellow
try {
    $response2 = Invoke-RestMethod -Uri $chatUrl -Method Post -Body $body -ContentType "application/json"
    Write-Host "响应:" -ForegroundColor Cyan
    $response2 | ConvertTo-Json
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

# 等待一下
Start-Sleep -Seconds 2

# 测试不同的问题
Write-Host "`n测试不同的问题..." -ForegroundColor Yellow
$body = @{
    userId = $userId
    sessionId = $sessionId
    message = "我可以使用什么支付方式？"
    context = @{}
} | ConvertTo-Json

Write-Host "发送请求: $body"
try {
    $response3 = Invoke-RestMethod -Uri $chatUrl -Method Post -Body $body -ContentType "application/json"
    Write-Host "响应:" -ForegroundColor Cyan
    $response3 | ConvertTo-Json
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}

Write-Host "`n测试完成" -ForegroundColor Green 