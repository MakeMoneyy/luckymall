# 招财商城订单API测试脚本（简化版）
param(
    [string]$BaseUrl = "http://localhost:8080"
)

$baseUrl = $BaseUrl.TrimEnd('/')
$userId = 1

Write-Host "=== 招财商城订单API测试 ===" -ForegroundColor Green
Write-Host "服务器地址: $baseUrl"
Write-Host "测试用户ID: $userId"
Write-Host ""

# 测试1: 添加商品到购物车
Write-Host "1. 添加商品到购物车" -ForegroundColor Yellow
try {
    $addRequest = @{
        productId = 1
        quantity = 2
    }
    $headers = @{'Content-Type' = 'application/json'}
    $body = $addRequest | ConvertTo-Json
    $response = Invoke-RestMethod -Uri "$baseUrl/api/cart/$userId/add" -Method POST -Body $body -Headers $headers
    
    Write-Host "✓ 添加购物车成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 添加购物车失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试2: 获取购物车商品
Write-Host "`n2. 获取购物车商品" -ForegroundColor Yellow
try {
    $cartResponse = Invoke-RestMethod -Uri "$baseUrl/api/cart/$userId" -Method GET
    if ($cartResponse.data.Count -gt 0) {
        $cartItemIds = @()
        $totalAmount = 0
        foreach ($item in $cartResponse.data) {
            $cartItemIds += $item.id
            $totalAmount += $item.product.price * $item.quantity
            Write-Host "  - 商品: $($item.product.name), 数量: $($item.quantity), 小计: ¥$($item.product.price * $item.quantity)" -ForegroundColor Cyan
        }
        Write-Host "✓ 获取购物车成功，总金额: ¥$totalAmount" -ForegroundColor Green
    } else {
        Write-Host "✗ 购物车为空" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 获取购物车失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试3: 创建订单
Write-Host "`n3. 创建订单" -ForegroundColor Yellow
if ($cartItemIds.Count -gt 0) {
    try {
        $orderRequest = @{
            cartItemIds = $cartItemIds
            addressId = 1
            paymentMethod = "ALIPAY"
            isInstallment = $false
            orderRemark = "测试订单"
            expectedAmount = $totalAmount
        }
        
        $headers = @{'Content-Type' = 'application/json'}
        $body = $orderRequest | ConvertTo-Json -Depth 3
        $response = Invoke-RestMethod -Uri "$baseUrl/api/orders/$userId" -Method POST -Body $body -Headers $headers
        
        if ($response.data) {
            Write-Host "✓ 订单创建成功" -ForegroundColor Green
            Write-Host "  - 订单号: $($response.data.orderNo)" -ForegroundColor Cyan
            Write-Host "  - 订单ID: $($response.data.orderId)" -ForegroundColor Cyan
            Write-Host "  - 总金额: ¥$($response.data.totalAmount)" -ForegroundColor Cyan
            $orderId = $response.data.orderId
        } else {
            Write-Host "✗ 订单创建失败: $($response.message)" -ForegroundColor Red
        }
    } catch {
        Write-Host "✗ 订单创建失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Response: $($_.Exception.Response)" -ForegroundColor Red
    }
}

# 测试4: 查询订单详情
if ($orderId) {
    Write-Host "`n4. 查询订单详情" -ForegroundColor Yellow
    try {
        $orderResponse = Invoke-RestMethod -Uri "$baseUrl/api/orders/$orderId" -Method GET
        if ($orderResponse.data) {
            Write-Host "✓ 查询订单成功" -ForegroundColor Green
            Write-Host "  - 订单状态: $($orderResponse.data.orderStatus)" -ForegroundColor Cyan
            Write-Host "  - 支付状态: $($orderResponse.data.paymentStatus)" -ForegroundColor Cyan
        }
    } catch {
        Write-Host "✗ 查询订单失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green 