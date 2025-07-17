# 订单API测试脚本
Write-Host "=== 招财商城订单API测试 ===" -ForegroundColor Green

# API基础URL
$baseUrl = "http://localhost:8080/api"
$userId = 1

# 测试1: 查询用户地址列表
Write-Host "`n1. 查询用户地址列表" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/addresses/user/$userId" -Method GET
    if ($response.success) {
        Write-Host "✓ 查询成功，地址数量: $($response.data.Count)" -ForegroundColor Green
        $response.data | ForEach-Object {
            Write-Host "  - 地址ID: $($_.id), 收货人: $($_.receiverName), 默认: $($_.isDefault)" -ForegroundColor Cyan
        }
    } else {
        Write-Host "✗ 查询失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试2: 查询购物车商品列表
Write-Host "`n2. 查询购物车商品列表" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/cart/$userId" -Method GET
    if ($response.success) {
        Write-Host "✓ 查询成功，购物车商品数量: $($response.data.Count)" -ForegroundColor Green
        $cartItemIds = @()
        $totalAmount = 0
        $response.data | ForEach-Object {
            Write-Host "  - 商品: $($_.product.name), 数量: $($_.quantity), 价格: ¥$($_.product.price)" -ForegroundColor Cyan
            $cartItemIds += $_.id
            $totalAmount += $_.product.price * $_.quantity
        }
        Write-Host "  总金额: ¥$totalAmount" -ForegroundColor Magenta
    } else {
        Write-Host "✗ 查询失败: $($response.message)" -ForegroundColor Red
        return
    }
} catch {
    Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
    return
}

# 测试3: 查询分期方案
Write-Host "`n3. 查询分期方案" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/installment-plans" -Method GET
    if ($response.success) {
        Write-Host "✓ 查询成功，分期方案数量: $($response.data.Count)" -ForegroundColor Green
        $response.data | ForEach-Object {
            Write-Host "  - $($_.planName): $($_.installmentCount)期, 利率: $($_.interestRate)%" -ForegroundColor Cyan
        }
    } else {
        Write-Host "✗ 查询失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试4: 查询可用分期方案（基于金额）
Write-Host "`n4. 查询可用分期方案（金额: ¥$totalAmount）" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/installment-plans/available?amount=$totalAmount" -Method GET
    if ($response.success) {
        Write-Host "✓ 查询成功，可用分期方案: $($response.data.Count)" -ForegroundColor Green
        $installmentPlanId = $null
        $response.data | ForEach-Object {
            Write-Host "  - $($_.planName): 每期约 ¥$([math]::Round($totalAmount / $_.installmentCount, 2))" -ForegroundColor Cyan
            if ($_.installmentCount -eq 3) {
                $installmentPlanId = $_.id
                Write-Host "    → 选择此方案用于测试" -ForegroundColor Yellow
            }
        }
    } else {
        Write-Host "✗ 查询失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试5: 创建订单（不分期）
Write-Host "`n5. 创建订单（不分期）" -ForegroundColor Yellow
if ($cartItemIds.Count -gt 0) {
    $orderRequest = @{
        cartItemIds = $cartItemIds
        addressId = 1
        paymentMethod = "ALIPAY"
        isInstallment = $false
        orderRemark = "测试订单 - 不分期"
        expectedAmount = $totalAmount
    }
    
    try {
        $headers = @{ 'Content-Type' = 'application/json' }
        $body = $orderRequest | ConvertTo-Json
        $response = Invoke-RestMethod -Uri "$baseUrl/orders/$userId" -Method POST -Body $body -Headers $headers
        
        if ($response.success) {
            Write-Host "✓ 订单创建成功" -ForegroundColor Green
            Write-Host "  - 订单号: $($response.data.orderNo)" -ForegroundColor Cyan
            Write-Host "  - 订单ID: $($response.data.orderId)" -ForegroundColor Cyan
            Write-Host "  - 总金额: ¥$($response.data.totalAmount)" -ForegroundColor Cyan
            Write-Host "  - 分期付款: $($response.data.isInstallment)" -ForegroundColor Cyan
            $orderId1 = $response.data.orderId
        } else {
            Write-Host "✗ 订单创建失败: $($response.message)" -ForegroundColor Red
        }
    } catch {
        Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试6: 创建订单（分期）
Write-Host "`n6. 创建订单（分期）" -ForegroundColor Yellow
if ($cartItemIds.Count -gt 0 -and $installmentPlanId) {
    # 重新添加商品到购物车用于第二个订单测试
    Write-Host "  重新添加商品到购物车..." -ForegroundColor Gray
    try {
        $addRequest = @{
            productId = 1
            quantity = 1
        }
        $headers = @{ 'Content-Type' = 'application/json' }
        $body = $addRequest | ConvertTo-Json
        $response = Invoke-RestMethod -Uri "$baseUrl/cart/$userId/add" -Method POST -Body $body -Headers $headers
        
        if ($response.success) {
            # 重新获取购物车商品
            $cartResponse = Invoke-RestMethod -Uri "$baseUrl/cart/$userId" -Method GET
            $newCartItemIds = @()
            $newTotalAmount = 0
            $cartResponse.data | ForEach-Object {
                $newCartItemIds += $_.id
                $newTotalAmount += $_.product.price * $_.quantity
            }
            
            $orderRequest = @{
                cartItemIds = $newCartItemIds
                addressId = 1
                paymentMethod = "WECHAT"
                isInstallment = $true
                installmentPlanId = $installmentPlanId
                orderRemark = "测试订单 - 3期免息"
                expectedAmount = $newTotalAmount
            }
            
            $body = $orderRequest | ConvertTo-Json
            $response = Invoke-RestMethod -Uri "$baseUrl/orders/$userId" -Method POST -Body $body -Headers $headers
            
            if ($response.success) {
                Write-Host "✓ 分期订单创建成功" -ForegroundColor Green
                Write-Host "  - 订单号: $($response.data.orderNo)" -ForegroundColor Cyan
                Write-Host "  - 分期期数: $($response.data.installmentCount)" -ForegroundColor Cyan
                Write-Host "  - 每期金额: ¥$($response.data.monthlyAmount)" -ForegroundColor Cyan
                $orderId2 = $response.data.orderId
            } else {
                Write-Host "✗ 分期订单创建失败: $($response.message)" -ForegroundColor Red
            }
        }
    } catch {
        Write-Host "✗ 分期订单测试失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试7: 查询用户订单列表
Write-Host "`n7. 查询用户订单列表" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/orders/user/$userId" -Method GET
    if ($response.success) {
        Write-Host "✓ 查询成功，订单数量: $($response.data.Count)" -ForegroundColor Green
        $response.data | ForEach-Object {
            Write-Host "  - 订单号: $($_.orderNo), 状态: $($_.orderStatus), 金额: ¥$($_.totalAmount)" -ForegroundColor Cyan
            if ($_.isInstallment -eq 1) {
                Write-Host "    分期: $($_.installmentCount)期，每期 ¥$($_.monthlyAmount)" -ForegroundColor Magenta
            }
        }
    } else {
        Write-Host "✗ 查询失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 