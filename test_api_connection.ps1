# API Connection Test Script
Write-Host "=== API Connection Test ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"

# Test if backend API is accessible
Write-Host "`n1. Testing backend API accessibility..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/products" -Method GET -ContentType "application/json" -TimeoutSec 5
    Write-Host "Success! Backend API is accessible" -ForegroundColor Green
    Write-Host "Response code: $($response.code)"
    Write-Host "Response message: $($response.message)"
} catch {
    Write-Host "Failed: Backend API is not accessible" -ForegroundColor Red
    Write-Host "Error message: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Cart API
Write-Host "`n2. Testing Cart API..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/cart/1" -Method GET -ContentType "application/json" -TimeoutSec 5
    Write-Host "Success! Cart API is accessible" -ForegroundColor Green
    Write-Host "Response code: $($response.code)"
    Write-Host "Response message: $($response.message)"
} catch {
    Write-Host "Failed: Cart API is not accessible" -ForegroundColor Red
    Write-Host "Error message: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Order API
Write-Host "`n3. Testing Order API..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/orders/user/1" -Method GET -ContentType "application/json" -TimeoutSec 5
    Write-Host "Success! Order API is accessible" -ForegroundColor Green
    Write-Host "Response code: $($response.code)"
    Write-Host "Response message: $($response.message)"
} catch {
    Write-Host "Failed: Order API is not accessible" -ForegroundColor Red
    Write-Host "Error message: $($_.Exception.Message)" -ForegroundColor Red
}

# Test User API
Write-Host "`n4. Testing User API..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/users/1" -Method GET -ContentType "application/json" -TimeoutSec 5
    Write-Host "Success! User API is accessible" -ForegroundColor Green
    Write-Host "Response code: $($response.code)"
    Write-Host "Response message: $($response.message)"
} catch {
    Write-Host "Failed: User API is not accessible" -ForegroundColor Red
    Write-Host "Error message: $($_.Exception.Message)" -ForegroundColor Red
}

# Test Payment API
Write-Host "`n5. Testing Payment API..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/payments/installment-plans" -Method GET -ContentType "application/json" -TimeoutSec 5 -Body @{ amount = 1000 }
    Write-Host "Success! Payment API is accessible" -ForegroundColor Green
    Write-Host "Response code: $($response.code)"
    Write-Host "Response message: $($response.message)"
} catch {
    Write-Host "Failed: Payment API is not accessible" -ForegroundColor Red
    Write-Host "Error message: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Test Completed ===" -ForegroundColor Green 