# 测试增强版智能客服系统
# 该脚本测试智能客服系统的各项功能，包括意图识别、情感分析、人工客服转接和缓存性能

# 设置基础变量
$baseUrl = "http://localhost:8080"
$userId = 1
$sessionId = "test-session-" + [DateTime]::Now.Ticks

# 辅助函数：格式化输出
function Write-TestResult {
    param (
        [string]$testName,
        [bool]$success,
        [string]$message = "",
        [object]$data = $null
    )
    
    if ($success) {
        Write-Host "✓ $testName 成功" -ForegroundColor Green
        if ($message) {
            Write-Host "  - $message" -ForegroundColor White
        }
    } else {
        Write-Host "✗ $testName 失败" -ForegroundColor Red
        if ($message) {
            Write-Host "  - $message" -ForegroundColor Red
        }
    }
    
    if ($data) {
        $dataJson = $data | ConvertTo-Json -Depth 5
        Write-Host "  数据: $dataJson" -ForegroundColor Gray
    }
    
    Write-Host ""
}

# 测试1: 基础对话测试
Write-Host "1. 基础对话测试..." -ForegroundColor Cyan
$chatRequest = @{
    userId = $userId
    sessionId = $sessionId
    message = "你好，我想问一下你们的退货政策"
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    Write-TestResult -testName "基础对话测试" -success $true -message "成功获取回复" -data @{
        response = $response.data.response
        intentType = $response.data.intentType
        emotionType = $response.data.emotionType
    }
} catch {
    Write-TestResult -testName "基础对话测试" -success $false -message $_.Exception.Message
}

# 测试2: 意图识别测试
Write-Host "2. 意图识别测试..." -ForegroundColor Cyan
$intentTests = @(
    @{
        message = "我想查询一下我的订单状态"
        expectedIntent = "ORDER_QUERY"
    },
    @{
        message = "这个商品什么时候能到货？"
        expectedIntent = "PRODUCT_QUERY"
    },
    @{
        message = "我可以分期付款吗？"
        expectedIntent = "PAYMENT"
    },
    @{
        message = "我的包裹什么时候能到？"
        expectedIntent = "LOGISTICS"
    },
    @{
        message = "我想退货"
        expectedIntent = "RETURN_REFUND"
    }
)

foreach ($test in $intentTests) {
    $chatRequest = @{
        userId = $userId
        sessionId = $sessionId
        message = $test.message
        context = @{
            currentPage = "home"
        }
    } | ConvertTo-Json -Depth 3

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
        $success = $response.data.intentType -eq $test.expectedIntent
        Write-TestResult -testName "意图识别测试: '$($test.message)'" -success $success -message "预期: $($test.expectedIntent), 实际: $($response.data.intentType)" -data @{
            confidence = $response.data.intentConfidence
        }
    } catch {
        Write-TestResult -testName "意图识别测试: '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 测试3: 情感分析测试
Write-Host "3. 情感分析测试..." -ForegroundColor Cyan
$emotionTests = @(
    @{
        message = "我非常喜欢你们的服务，太棒了！"
        expectedEmotion = "POSITIVE"
    },
    @{
        message = "这个商品什么时候能到货？"
        expectedEmotion = "NEUTRAL"
    },
    @{
        message = "我已经等了一周了，这也太慢了吧，太差劲了！"
        expectedEmotion = "NEGATIVE"
    }
)

foreach ($test in $emotionTests) {
    $chatRequest = @{
        userId = $userId
        sessionId = $sessionId
        message = $test.message
        context = @{
            currentPage = "home"
        }
    } | ConvertTo-Json -Depth 3

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
        $success = $response.data.emotionType -eq $test.expectedEmotion
        Write-TestResult -testName "情感分析测试: '$($test.message)'" -success $success -message "预期: $($test.expectedEmotion), 实际: $($response.data.emotionType)" -data @{
            intensity = $response.data.emotionIntensity
        }
    } catch {
        Write-TestResult -testName "情感分析测试: '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 测试4: 人工客服转接测试
Write-Host "4. 人工客服转接测试..." -ForegroundColor Cyan
$transferSessionId = "transfer-session-" + [DateTime]::Now.Ticks

# 4.1 创建人工客服转接请求
$transferRequest = @{
    userId = $userId
    sessionId = $transferSessionId
    reason = "需要专业帮助"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/human-service/transfer" -Method POST -Body $transferRequest -ContentType "application/json"
    Write-TestResult -testName "创建人工客服转接请求" -success $true -message "成功创建转接请求" -data @{
        sessionId = $response.data.sessionId
        status = $response.data.status
        queuePosition = $response.data.queuePosition
    }
    
    # 保存转接会话ID
    $humanServiceSessionId = $response.data.sessionId
} catch {
    Write-TestResult -testName "创建人工客服转接请求" -success $false -message $_.Exception.Message
}

# 4.2 查询转接状态
if ($humanServiceSessionId) {
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/human-service/status?sessionId=$humanServiceSessionId" -Method GET
        Write-TestResult -testName "查询转接状态" -success $true -message "成功查询转接状态" -data @{
            status = $response.data.status
            queuePosition = $response.data.queuePosition
            estimatedWaitTime = $response.data.estimatedWaitTime
        }
    } catch {
        Write-TestResult -testName "查询转接状态" -success $false -message $_.Exception.Message
    }
}

# 测试5: 结构化信息卡片测试
Write-Host "5. 结构化信息卡片测试..." -ForegroundColor Cyan
$cardTests = @(
    @{
        message = "我想查询信用卡积分"
        expectedCardType = "CREDIT_CARD"
    },
    @{
        message = "有什么促销活动？"
        expectedCardType = "PROMOTION"
    }
)

foreach ($test in $cardTests) {
    $chatRequest = @{
        userId = $userId
        sessionId = $sessionId
        message = $test.message
        context = @{
            currentPage = "home"
        }
    } | ConvertTo-Json -Depth 3

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
        $hasCard = $null -ne $response.data.structuredCards -and $response.data.structuredCards.Count -gt 0
        Write-TestResult -testName "结构化信息卡片测试: '$($test.message)'" -success $hasCard -message "是否返回卡片: $hasCard" -data @{
            cards = $response.data.structuredCards
        }
    } catch {
        Write-TestResult -testName "结构化信息卡片测试: '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 测试6: 上下文理解测试
Write-Host "6. 上下文理解测试..." -ForegroundColor Cyan
$contextSessionId = "context-session-" + [DateTime]::Now.Ticks

# 6.1 第一条消息
$chatRequest1 = @{
    userId = $userId
    sessionId = $contextSessionId
    message = "我想查询一下iPhone 15的价格"
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json -Depth 3

try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest1 -ContentType "application/json"
    Write-TestResult -testName "上下文理解测试(1)" -success $true -message "成功获取第一条回复" -data @{
        response = $response1.data.response
    }
    
    # 6.2 第二条消息（依赖上下文）
    $chatRequest2 = @{
        userId = $userId
        sessionId = $contextSessionId
        message = "它有什么颜色？"
        context = @{
            currentPage = "product"
            productId = 15  # 假设这是iPhone 15的ID
        }
    } | ConvertTo-Json -Depth 3
    
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest2 -ContentType "application/json"
    
    # 检查回复中是否包含"颜色"相关信息
    $containsColorInfo = $response2.data.response -match "颜色"
    Write-TestResult -testName "上下文理解测试(2)" -success $containsColorInfo -message "回复中是否包含颜色信息: $containsColorInfo" -data @{
        response = $response2.data.response
    }
} catch {
    Write-TestResult -testName "上下文理解测试" -success $false -message $_.Exception.Message
}

# 测试7: 实体提取测试
Write-Host "7. 实体提取测试..." -ForegroundColor Cyan
$entityTests = @(
    @{
        message = "我想查询订单号为123456的状态"
        expectedEntity = "orderNumber"
        expectedValue = "123456"
    },
    @{
        message = "我想了解一下商品ID为789的详情"
        expectedEntity = "productId"
        expectedValue = "789"
    }
)

foreach ($test in $entityTests) {
    $chatRequest = @{
        userId = $userId
        sessionId = $sessionId
        message = $test.message
        context = @{
            currentPage = "home"
        }
    } | ConvertTo-Json -Depth 3

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
        
        # 检查提取的实体
        $extractedEntities = $response.data.extractedEntities
        $hasExpectedEntity = $null -ne $extractedEntities -and $extractedEntities.ContainsKey($test.expectedEntity) -and $extractedEntities[$test.expectedEntity] -eq $test.expectedValue
        
        Write-TestResult -testName "实体提取测试: '$($test.message)'" -success $hasExpectedEntity -message "预期实体: $($test.expectedEntity)=$($test.expectedValue)" -data @{
            extractedEntities = $extractedEntities
        }
    } catch {
        Write-TestResult -testName "实体提取测试: '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 测试8: 缓存性能测试
Write-Host "8. 缓存性能测试..." -ForegroundColor Cyan
$chatRequest = @{
    userId = $userId
    sessionId = $sessionId
    message = "你们的退货政策是什么？"
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json -Depth 3

try {
    # 第一次请求
    $startTime = Get-Date
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $firstRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    # 第二次请求（应该命中缓存）
    $startTime = Get-Date
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $secondRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    Write-TestResult -testName "缓存性能测试" -success $true -message "测试完成" -data @{
        firstRequestTime = [Math]::Round($firstRequestTime, 2)
        secondRequestTime = [Math]::Round($secondRequestTime, 2)
        cacheHit = $response2.data.cacheHit
    }
    
    if ($response2.data.cacheHit -and $secondRequestTime -lt $firstRequestTime) {
        Write-Host "  ✓ 缓存机制工作正常，性能提升明显" -ForegroundColor Green
        $improvement = (1 - ($secondRequestTime / $firstRequestTime)) * 100
        Write-Host "  - 性能提升: $([Math]::Round($improvement, 2))%" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ 缓存机制可能未正常工作" -ForegroundColor Yellow
    }
} catch {
    Write-TestResult -testName "缓存性能测试" -success $false -message $_.Exception.Message
}

Write-Host "测试完成!" -ForegroundColor Cyan 