# 完整的订单创建和查询测试
$baseUrl = "http://localhost:8080"
$userId = 1

Write-Host "=== 订单流程完整测试 ===" -ForegroundColor Green

# 1. 清理用户购物车
Write-Host "`n1. 清理购物车" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/api/cart/$userId/clear" -Method POST -ContentType "application/json" -Body "{}"
    Write-Host "购物车已清理" -ForegroundColor Green
} catch {
    Write-Host "清理购物车失败或购物车已空: $($_.Exception.Message)" -ForegroundColor Yellow
}

# 2. 添加商品到购物车
Write-Host "`n2. 添加商品到购物车" -ForegroundColor Yellow
$addRequest = @{
    productId = 1
    quantity = 2
} | ConvertTo-Json

try {
    $addResponse = Invoke-RestMethod -Uri "$baseUrl/api/cart/$userId/add" -Method POST -Body $addRequest -ContentType "application/json"
    Write-Host "添加商品成功: $($addResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "添加商品失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 查询购物车
Write-Host "`n3. 查询购物车" -ForegroundColor Yellow
try {
    $cartResponse = Invoke-RestMethod -Uri "$baseUrl/api/cart/$userId" -Method GET
    
    if ($cartResponse.data -and $cartResponse.data.Count -gt 0) {
        $cartItemIds = @()
        $totalAmount = 0
        foreach ($item in $cartResponse.data) {
            $cartItemIds += $item.id
            $totalAmount += $item.product.price * $item.quantity
            Write-Host "  商品: $($item.product.name), 数量: $($item.quantity), 价格: ¥$($item.product.price)" -ForegroundColor Cyan
        }
        Write-Host "购物车总金额: ¥$totalAmount" -ForegroundColor Green
    } else {
        Write-Host "购物车为空，无法继续测试" -ForegroundColor Red
        exit
    }
} catch {
    Write-Host "查询购物车失败: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# 4. 查询订单创建前的订单列表
Write-Host "`n4. 查询当前订单列表（创建前）" -ForegroundColor Yellow
try {
    $beforeOrders = Invoke-RestMethod -Uri "$baseUrl/api/orders/user/$userId" -Method GET
    $beforeCount = if ($beforeOrders.data) { $beforeOrders.data.Count } else { 0 }
    Write-Host "创建前订单数量: $beforeCount" -ForegroundColor Cyan
} catch {
    Write-Host "查询订单失败: $($_.Exception.Message)" -ForegroundColor Red
    $beforeCount = 0
}

# 5. 创建订单
Write-Host "`n5. 创建订单" -ForegroundColor Yellow
$orderRequest = @{
    cartItemIds = $cartItemIds
    addressId = 1
    paymentMethod = "ALIPAY"
    isInstallment = $false
    orderRemark = "完整流程测试订单"
    expectedAmount = $totalAmount
} | ConvertTo-Json

Write-Host "订单请求:" -ForegroundColor Gray
Write-Host $orderRequest -ForegroundColor Gray

try {
    $orderResponse = Invoke-RestMethod -Uri "$baseUrl/api/orders/$userId" -Method POST -Body $orderRequest -ContentType "application/json"
    
    Write-Host "订单创建响应:" -ForegroundColor Cyan
    Write-Host ($orderResponse | ConvertTo-Json -Depth 2) -ForegroundColor Gray
    
    if ($orderResponse.data -and $orderResponse.data.orderId) {
        Write-Host "✓ 订单创建成功!" -ForegroundColor Green
        Write-Host "  订单号: $($orderResponse.data.orderNo)" -ForegroundColor Cyan
        Write-Host "  订单ID: $($orderResponse.data.orderId)" -ForegroundColor Cyan
        $newOrderId = $orderResponse.data.orderId
    } else {
        Write-Host "✗ 订单创建失败: 响应格式不正确" -ForegroundColor Red
        Write-Host "响应内容: $($orderResponse | ConvertTo-Json)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 订单创建失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "详细错误: $responseBody" -ForegroundColor Red
    }
}

# 6. 等待一下再查询
Write-Host "`n6. 等待2秒后查询订单列表..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

# 7. 查询订单列表（创建后）
Write-Host "`n7. 查询订单列表（创建后）" -ForegroundColor Yellow
try {
    $afterOrders = Invoke-RestMethod -Uri "$baseUrl/api/orders/user/$userId" -Method GET
    $afterCount = if ($afterOrders.data) { $afterOrders.data.Count } else { 0 }
    
    Write-Host "创建后订单数量: $afterCount" -ForegroundColor Cyan
    Write-Host "新增订单数量: $($afterCount - $beforeCount)" -ForegroundColor Cyan
    
    if ($afterOrders.data -and $afterOrders.data.Count -gt 0) {
        Write-Host "`n最新的几个订单:" -ForegroundColor Cyan
        $latestOrders = $afterOrders.data | Select-Object -First 3
        foreach ($order in $latestOrders) {
            Write-Host "  - 订单号: $($order.orderNo), 金额: ¥$($order.totalAmount), 状态: $($order.orderStatus), 创建时间: $($order.createdAt)" -ForegroundColor White
        }
    } else {
        Write-Host "没有找到任何订单!" -ForegroundColor Red
    }
    
    # 检查新创建的订单是否在列表中
    if ($newOrderId) {
        $foundOrder = $afterOrders.data | Where-Object { $_.id -eq $newOrderId }
        if ($foundOrder) {
            Write-Host "✓ 新创建的订单已在列表中找到!" -ForegroundColor Green
        } else {
            Write-Host "✗ 新创建的订单未在列表中找到!" -ForegroundColor Red
        }
    }
    
} catch {
    Write-Host "查询订单列表失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 