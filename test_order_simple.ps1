# 简化的订单API测试脚本
Write-Host "=== 招财商城订单API测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080/api"
$userId = 1

# 测试1: 查询用户地址
Write-Host "`n1. 查询用户地址" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/addresses/user/$userId" -Method GET
    Write-Host "地址数量: $($response.data.Count)" -ForegroundColor Green
} catch {
    Write-Host "请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试2: 查询购物车
Write-Host "`n2. 查询购物车" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/cart/$userId" -Method GET
    Write-Host "购物车商品数量: $($response.data.Count)" -ForegroundColor Green
    
    if ($response.data.Count -gt 0) {
        $cartItemIds = @()
        $totalAmount = 0
        foreach ($item in $response.data) {
            $cartItemIds += $item.id
            $totalAmount += $item.product.price * $item.quantity
            Write-Host "  - $($item.product.name): $($item.quantity)个, ¥$($item.product.price)" -ForegroundColor Cyan
        }
        Write-Host "总金额: ¥$totalAmount" -ForegroundColor Magenta
    }
} catch {
    Write-Host "请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试3: 查询分期方案
Write-Host "`n3. 查询分期方案" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/installment-plans" -Method GET
    Write-Host "分期方案数量: $($response.data.Count)" -ForegroundColor Green
    foreach ($plan in $response.data) {
        Write-Host "  - $($plan.planName): $($plan.installmentCount)期" -ForegroundColor Cyan
    }
} catch {
    Write-Host "请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试4: 创建订单
Write-Host "`n4. 创建订单测试" -ForegroundColor Yellow
if ($cartItemIds -and $cartItemIds.Count -gt 0) {
    $orderData = @{
        cartItemIds = $cartItemIds
        addressId = 1
        paymentMethod = "ALIPAY"
        isInstallment = $false
        orderRemark = "测试订单"
        expectedAmount = $totalAmount
    }
    
    $json = $orderData | ConvertTo-Json
    $headers = @{'Content-Type' = 'application/json'}
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/orders/$userId" -Method POST -Body $json -Headers $headers
        if ($response.success) {
            Write-Host "✓ 订单创建成功!" -ForegroundColor Green
            Write-Host "  订单号: $($response.data.orderNo)" -ForegroundColor Cyan
            Write-Host "  总金额: ¥$($response.data.totalAmount)" -ForegroundColor Cyan
        } else {
            Write-Host "✗ 订单创建失败: $($response.message)" -ForegroundColor Red
        }
    } catch {
        Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "购物车为空，跳过订单创建测试" -ForegroundColor Yellow
}

# 测试5: 查询订单列表
Write-Host "`n5. 查询订单列表" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/user/$userId" -Method GET
    if ($response.success) {
        Write-Host "✓ 查询成功，订单数量: $($response.data.Count)" -ForegroundColor Green
        foreach ($order in $response.data) {
            Write-Host "  - $($order.orderNo): $($order.orderStatus), ¥$($order.totalAmount)" -ForegroundColor Cyan
            if ($order.isInstallment -eq 1) {
                Write-Host "    分期: $($order.installmentCount)期，每期 ¥$($order.monthlyAmount)" -ForegroundColor Magenta
            }
        }
    }
} catch {
    Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 