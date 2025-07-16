# 购物车API测试脚本
$baseUrl = "http://localhost:8080/api/cart"

Write-Host "=== 购物车API测试 ===" -ForegroundColor Green

# 测试1: 获取用户1的购物车
Write-Host "`n1. 获取用户1的购物车..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -ContentType "application/json"
    Write-Host "成功! 购物车商品数量: $($response.data.Count)" -ForegroundColor Green
    $response.data | ForEach-Object { 
        Write-Host "  - $($_.product.name) x $($_.quantity) = ¥$($_.product.price * $_.quantity)" 
    }
} catch {
    Write-Host "失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试2: 获取购物车商品数量
Write-Host "`n2. 获取用户1购物车商品数量..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/1/count" -Method GET -ContentType "application/json"
    Write-Host "成功! 商品数量: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试3: 添加商品到购物车
Write-Host "`n3. 添加iPad Pro到用户1购物车..." -ForegroundColor Yellow
try {
    $addRequest = @{
        productId = 13
        quantity = 1
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/1/add" -Method POST -Body $addRequest -ContentType "application/json"
    Write-Host "成功! $($response.message)" -ForegroundColor Green
} catch {
    Write-Host "失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试4: 再次获取购物车验证添加结果
Write-Host "`n4. 验证添加结果..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/1" -Method GET -ContentType "application/json"
    Write-Host "成功! 当前购物车商品数量: $($response.data.Count)" -ForegroundColor Green
    $response.data | ForEach-Object { 
        Write-Host "  - $($_.product.name) x $($_.quantity)" 
    }
} catch {
    Write-Host "失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 