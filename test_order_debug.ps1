# 测试订单创建API
$baseUrl = "http://localhost:8080"
$userId = 1

Write-Host "=== 测试订单创建API ===" -ForegroundColor Green

# 1. 测试添加商品到购物车
Write-Host "`n1. 添加商品到购物车" -ForegroundColor Yellow
$addRequest = @{
    productId = 1
    quantity = 1
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/cart/$userId/add" -Method POST -Body $addRequest -ContentType "application/json"
    Write-Host "添加购物车成功" -ForegroundColor Green
} catch {
    Write-Host "添加购物车失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2. 获取购物车商品
Write-Host "`n2. 获取购物车商品" -ForegroundColor Yellow
try {
    $cartResponse = Invoke-RestMethod -Uri "$baseUrl/api/cart/$userId" -Method GET
    $cartItemIds = @()
    $totalAmount = 0
    
    if ($cartResponse.data -and $cartResponse.data.Count -gt 0) {
        foreach ($item in $cartResponse.data) {
            $cartItemIds += $item.id
            $totalAmount += $item.product.price * $item.quantity
            Write-Host "商品: $($item.product.name), 数量: $($item.quantity)" -ForegroundColor Cyan
        }
        Write-Host "购物车总金额: $totalAmount" -ForegroundColor Green
    } else {
        Write-Host "购物车为空" -ForegroundColor Red
        exit
    }
} catch {
    Write-Host "获取购物车失败: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# 3. 创建订单
Write-Host "`n3. 创建订单" -ForegroundColor Yellow
$orderRequest = @{
    cartItemIds = $cartItemIds
    addressId = 1
    paymentMethod = "ALIPAY"
    isInstallment = $false
    orderRemark = "测试订单"
    expectedAmount = $totalAmount
} | ConvertTo-Json

Write-Host "订单请求数据:" -ForegroundColor Cyan
Write-Host $orderRequest -ForegroundColor Gray

try {
    $orderResponse = Invoke-RestMethod -Uri "$baseUrl/api/orders/$userId" -Method POST -Body $orderRequest -ContentType "application/json"
    
    Write-Host "API响应:" -ForegroundColor Cyan
    Write-Host ($orderResponse | ConvertTo-Json -Depth 3) -ForegroundColor Gray
    
    if ($orderResponse.data) {
        Write-Host "订单创建成功!" -ForegroundColor Green
        Write-Host "订单号: $($orderResponse.data.orderNo)" -ForegroundColor Cyan
        Write-Host "订单ID: $($orderResponse.data.orderId)" -ForegroundColor Cyan
    } else {
        Write-Host "订单创建失败: 响应中没有data字段" -ForegroundColor Red
    }
} catch {
    Write-Host "订单创建失败:" -ForegroundColor Red
    Write-Host "错误消息: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应内容: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 