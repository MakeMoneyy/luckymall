# Simple Test Script
$baseUrl = "http://localhost:8080"
$userId = 1
$sessionId = "test-" + [DateTime]::Now.Ticks

Write-Host "Starting tests..." -ForegroundColor Cyan

# Test 1: Basic Chat
Write-Host "1. Basic Chat Test..." -ForegroundColor Cyan
$chatRequest = @{
    userId = $userId
    sessionId = $sessionId
    message = "Hello, I want to know about your return policy"
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    Write-Host "✓ Basic Chat Test Successful" -ForegroundColor Green
    Write-Host "  - Response: $($response.data.response)" -ForegroundColor White
} catch {
    Write-Host "✗ Basic Chat Test Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Cache Performance
Write-Host "2. Cache Performance Test..." -ForegroundColor Cyan
try {
    # First request
    $startTime = Get-Date
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $firstRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    # Second request (should hit cache)
    $startTime = Get-Date
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $secondRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    Write-Host "✓ Cache Performance Test Successful" -ForegroundColor Green
    Write-Host "  - First Request Time: $([Math]::Round($firstRequestTime, 2))ms" -ForegroundColor White
    Write-Host "  - Second Request Time: $([Math]::Round($secondRequestTime, 2))ms" -ForegroundColor White
    Write-Host "  - Cache Hit: $($response2.data.cacheHit)" -ForegroundColor White
    
    if ($response2.data.cacheHit -and $secondRequestTime -lt $firstRequestTime) {
        Write-Host "  ✓ Cache mechanism working properly, performance improved" -ForegroundColor Green
        $improvement = (1 - ($secondRequestTime / $firstRequestTime)) * 100
        Write-Host "  - Performance Improvement: $([Math]::Round($improvement, 2))%" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ Cache mechanism might not be working properly" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Cache Performance Test Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Human Service Transfer
Write-Host "3. Human Service Transfer Test..." -ForegroundColor Cyan
$transferSessionId = "transfer-" + [DateTime]::Now.Ticks

$transferRequest = @{
    userId = $userId
    sessionId = $transferSessionId
    reason = "Need professional help"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/human-service/transfer" -Method POST -Body $transferRequest -ContentType "application/json"
    Write-Host "✓ Human Service Transfer Request Successful" -ForegroundColor Green
    Write-Host "  - Session ID: $($response.data.sessionId)" -ForegroundColor White
    Write-Host "  - Status: $($response.data.status)" -ForegroundColor White
    
    # Save transfer session ID
    $humanServiceSessionId = $response.data.sessionId
    
    # Query transfer status
    if ($humanServiceSessionId) {
        $statusResponse = Invoke-RestMethod -Uri "$baseUrl/api/human-service/status?sessionId=$humanServiceSessionId" -Method GET
        Write-Host "  - Queue Position: $($statusResponse.data.queuePosition)" -ForegroundColor White
        Write-Host "  - Estimated Wait Time: $($statusResponse.data.estimatedWaitTime) minutes" -ForegroundColor White
    }
} catch {
    Write-Host "✗ Human Service Transfer Test Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Tests completed!" -ForegroundColor Cyan 