# 娴嬭瘯澧炲己鐗堟櫤鑳藉鏈嶇郴缁?
# 璇ヨ剼鏈祴璇曟櫤鑳藉鏈嶇郴缁熺殑鍚勯」鍔熻兘锛屽寘鎷剰鍥捐瘑鍒€佹儏鎰熷垎鏋愩€佷汉宸ュ鏈嶈浆鎺ュ拰缂撳瓨鎬ц兘

# 璁剧疆鍩虹鍙橀噺
$baseUrl = "http://localhost:8080"
$userId = 1
$sessionId = "test-session-" + [DateTime]::Now.Ticks

# 杈呭姪鍑芥暟锛氭牸寮忓寲杈撳嚭
function Write-TestResult {
    param (
        [string]$testName,
        [bool]$success,
        [string]$message = "",
        [object]$data = $null
    )
    
    if ($success) {
        Write-Host "鉁?$testName 鎴愬姛" -ForegroundColor Green
        if ($message) {
            Write-Host "  - $message" -ForegroundColor White
        }
    } else {
        Write-Host "鉁?$testName 澶辫触" -ForegroundColor Red
        if ($message) {
            Write-Host "  - $message" -ForegroundColor Red
        }
    }
    
    if ($data) {
        $dataJson = $data | ConvertTo-Json -Depth 5
        Write-Host "  鏁版嵁: $dataJson" -ForegroundColor Gray
    }
    
    Write-Host ""
}

# 娴嬭瘯1: 鍩虹瀵硅瘽娴嬭瘯
Write-Host "1. 鍩虹瀵硅瘽娴嬭瘯..." -ForegroundColor Cyan
$chatRequest = @{
    userId = $userId
    sessionId = $sessionId
    message = "浣犲ソ锛屾垜鎯抽棶涓€涓嬩綘浠殑閫€璐ф斂绛?
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    Write-TestResult -testName "鍩虹瀵硅瘽娴嬭瘯" -success $true -message "鎴愬姛鑾峰彇鍥炲" -data @{
        response = $response.data.response
        intentType = $response.data.intentType
        emotionType = $response.data.emotionType
    }
} catch {
    Write-TestResult -testName "鍩虹瀵硅瘽娴嬭瘯" -success $false -message $_.Exception.Message
}

# 娴嬭瘯2: 鎰忓浘璇嗗埆娴嬭瘯
Write-Host "2. 鎰忓浘璇嗗埆娴嬭瘯..." -ForegroundColor Cyan
$intentTests = @(
    @{
        message = "鎴戞兂鏌ヨ涓€涓嬫垜鐨勮鍗曠姸鎬?
        expectedIntent = "ORDER_QUERY"
    },
    @{
        message = "杩欎釜鍟嗗搧浠€涔堟椂鍊欒兘鍒拌揣锛?
        expectedIntent = "PRODUCT_QUERY"
    },
    @{
        message = "鎴戝彲浠ュ垎鏈熶粯娆惧悧锛?
        expectedIntent = "PAYMENT"
    },
    @{
        message = "鎴戠殑鍖呰９浠€涔堟椂鍊欒兘鍒帮紵"
        expectedIntent = "LOGISTICS"
    },
    @{
        message = "鎴戞兂閫€璐?
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
        Write-TestResult -testName "鎰忓浘璇嗗埆娴嬭瘯: '$($test.message)'" -success $success -message "棰勬湡: $($test.expectedIntent), 瀹為檯: $($response.data.intentType)" -data @{
            confidence = $response.data.intentConfidence
        }
    } catch {
        Write-TestResult -testName "鎰忓浘璇嗗埆娴嬭瘯: '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 娴嬭瘯3: 鎯呮劅鍒嗘瀽娴嬭瘯
Write-Host "3. 鎯呮劅鍒嗘瀽娴嬭瘯..." -ForegroundColor Cyan
$emotionTests = @(
    @{
        message = "鎴戦潪甯稿枩娆綘浠殑鏈嶅姟锛屽お妫掍簡锛?
        expectedEmotion = "POSITIVE"
    },
    @{
        message = "杩欎釜鍟嗗搧浠€涔堟椂鍊欒兘鍒拌揣锛?
        expectedEmotion = "NEUTRAL"
    },
    @{
        message = "鎴戝凡缁忕瓑浜嗕竴鍛ㄤ簡锛岃繖涔熷お鎱簡鍚э紝澶樊鍔蹭簡锛?
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
        Write-TestResult -testName "鎯呮劅鍒嗘瀽娴嬭瘯: '$($test.message)'" -success $success -message "棰勬湡: $($test.expectedEmotion), 瀹為檯: $($response.data.emotionType)" -data @{
            intensity = $response.data.emotionIntensity
        }
    } catch {
        Write-TestResult -testName "鎯呮劅鍒嗘瀽娴嬭瘯: '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 娴嬭瘯4: 浜哄伐瀹㈡湇杞帴娴嬭瘯
Write-Host "4. 浜哄伐瀹㈡湇杞帴娴嬭瘯..." -ForegroundColor Cyan
$transferSessionId = "transfer-session-" + [DateTime]::Now.Ticks

# 4.1 鍒涘缓浜哄伐瀹㈡湇杞帴璇锋眰
$transferRequest = @{
    userId = $userId
    sessionId = $transferSessionId
    reason = "闇€瑕佷笓涓氬府鍔?
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/human-service/transfer" -Method POST -Body $transferRequest -ContentType "application/json"
    Write-TestResult -testName "鍒涘缓浜哄伐瀹㈡湇杞帴璇锋眰" -success $true -message "鎴愬姛鍒涘缓杞帴璇锋眰" -data @{
        sessionId = $response.data.sessionId
        status = $response.data.status
        queuePosition = $response.data.queuePosition
    }
    
    # 淇濆瓨杞帴浼氳瘽ID
    $humanServiceSessionId = $response.data.sessionId
} catch {
    Write-TestResult -testName "鍒涘缓浜哄伐瀹㈡湇杞帴璇锋眰" -success $false -message $_.Exception.Message
}

# 4.2 鏌ヨ杞帴鐘舵€?
if ($humanServiceSessionId) {
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/human-service/status?sessionId=$humanServiceSessionId" -Method GET
        Write-TestResult -testName "鏌ヨ杞帴鐘舵€? -success $true -message "鎴愬姛鏌ヨ杞帴鐘舵€? -data @{
            status = $response.data.status
            queuePosition = $response.data.queuePosition
            estimatedWaitTime = $response.data.estimatedWaitTime
        }
    } catch {
        Write-TestResult -testName "鏌ヨ杞帴鐘舵€? -success $false -message $_.Exception.Message
    }
}

# 娴嬭瘯5: 缁撴瀯鍖栦俊鎭崱鐗囨祴璇?
Write-Host "5. 缁撴瀯鍖栦俊鎭崱鐗囨祴璇?.." -ForegroundColor Cyan
$cardTests = @(
    @{
        message = "鎴戞兂鏌ヨ淇＄敤鍗＄Н鍒?
        expectedCardType = "CREDIT_CARD"
    },
    @{
        message = "鏈変粈涔堜績閿€娲诲姩锛?
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
        Write-TestResult -testName "缁撴瀯鍖栦俊鎭崱鐗囨祴璇? '$($test.message)'" -success $hasCard -message "鏄惁杩斿洖鍗＄墖: $hasCard" -data @{
            cards = $response.data.structuredCards
        }
    } catch {
        Write-TestResult -testName "缁撴瀯鍖栦俊鎭崱鐗囨祴璇? '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 娴嬭瘯6: 涓婁笅鏂囩悊瑙ｆ祴璇?
Write-Host "6. 涓婁笅鏂囩悊瑙ｆ祴璇?.." -ForegroundColor Cyan
$contextSessionId = "context-session-" + [DateTime]::Now.Ticks

# 6.1 绗竴鏉℃秷鎭?
$chatRequest1 = @{
    userId = $userId
    sessionId = $contextSessionId
    message = "鎴戞兂鏌ヨ涓€涓媔Phone 15鐨勪环鏍?
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json -Depth 3

try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest1 -ContentType "application/json"
    Write-TestResult -testName "涓婁笅鏂囩悊瑙ｆ祴璇?1)" -success $true -message "鎴愬姛鑾峰彇绗竴鏉″洖澶? -data @{
        response = $response1.data.response
    }
    
    # 6.2 绗簩鏉℃秷鎭紙渚濊禆涓婁笅鏂囷級
    $chatRequest2 = @{
        userId = $userId
        sessionId = $contextSessionId
        message = "瀹冩湁浠€涔堥鑹诧紵"
        context = @{
            currentPage = "product"
            productId = 15  # 鍋囪杩欐槸iPhone 15鐨処D
        }
    } | ConvertTo-Json -Depth 3
    
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest2 -ContentType "application/json"
    
    # 妫€鏌ュ洖澶嶄腑鏄惁鍖呭惈"棰滆壊"鐩稿叧淇℃伅
    $containsColorInfo = $response2.data.response -match "棰滆壊"
    Write-TestResult -testName "涓婁笅鏂囩悊瑙ｆ祴璇?2)" -success $containsColorInfo -message "鍥炲涓槸鍚﹀寘鍚鑹蹭俊鎭? $containsColorInfo" -data @{
        response = $response2.data.response
    }
} catch {
    Write-TestResult -testName "涓婁笅鏂囩悊瑙ｆ祴璇? -success $false -message $_.Exception.Message
}

# 娴嬭瘯7: 瀹炰綋鎻愬彇娴嬭瘯
Write-Host "7. 瀹炰綋鎻愬彇娴嬭瘯..." -ForegroundColor Cyan
$entityTests = @(
    @{
        message = "鎴戞兂鏌ヨ璁㈠崟鍙蜂负123456鐨勭姸鎬?
        expectedEntity = "orderNumber"
        expectedValue = "123456"
    },
    @{
        message = "鎴戞兂浜嗚В涓€涓嬪晢鍝両D涓?89鐨勮鎯?
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
        
        # 妫€鏌ユ彁鍙栫殑瀹炰綋
        $extractedEntities = $response.data.extractedEntities
        $hasExpectedEntity = $null -ne $extractedEntities -and $extractedEntities.ContainsKey($test.expectedEntity) -and $extractedEntities[$test.expectedEntity] -eq $test.expectedValue
        
        Write-TestResult -testName "瀹炰綋鎻愬彇娴嬭瘯: '$($test.message)'" -success $hasExpectedEntity -message "棰勬湡瀹炰綋: $($test.expectedEntity)=$($test.expectedValue)" -data @{
            extractedEntities = $extractedEntities
        }
    } catch {
        Write-TestResult -testName "瀹炰綋鎻愬彇娴嬭瘯: '$($test.message)'" -success $false -message $_.Exception.Message
    }
}

# 娴嬭瘯8: 缂撳瓨鎬ц兘娴嬭瘯
Write-Host "8. 缂撳瓨鎬ц兘娴嬭瘯..." -ForegroundColor Cyan
$chatRequest = @{
    userId = $userId
    sessionId = $sessionId
    message = "浣犱滑鐨勯€€璐ф斂绛栨槸浠€涔堬紵"
    context = @{
        currentPage = "home"
    }
} | ConvertTo-Json -Depth 3

try {
    # 绗竴娆¤姹?
    $startTime = Get-Date
    $response1 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $firstRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    # 绗簩娆¤姹傦紙搴旇鍛戒腑缂撳瓨锛?
    $startTime = Get-Date
    $response2 = Invoke-RestMethod -Uri "$baseUrl/api/customer-service/chat" -Method POST -Body $chatRequest -ContentType "application/json"
    $secondRequestTime = ((Get-Date) - $startTime).TotalMilliseconds
    
    Write-TestResult -testName "缂撳瓨鎬ц兘娴嬭瘯" -success $true -message "娴嬭瘯瀹屾垚" -data @{
        firstRequestTime = [Math]::Round($firstRequestTime, 2)
        secondRequestTime = [Math]::Round($secondRequestTime, 2)
        cacheHit = $response2.data.cacheHit
    }
    
    if ($response2.data.cacheHit -and $secondRequestTime -lt $firstRequestTime) {
        Write-Host "  鉁?缂撳瓨鏈哄埗宸ヤ綔姝ｅ父锛屾€ц兘鎻愬崌鏄庢樉" -ForegroundColor Green
        $improvement = (1 - ($secondRequestTime / $firstRequestTime)) * 100
        Write-Host "  - 鎬ц兘鎻愬崌: $([Math]::Round($improvement, 2))%" -ForegroundColor Green
    } else {
        Write-Host "  鈿?缂撳瓨鏈哄埗鍙兘鏈甯稿伐浣? -ForegroundColor Yellow
    }
} catch {
    Write-TestResult -testName "缂撳瓨鎬ц兘娴嬭瘯" -success $false -message $_.Exception.Message
}

Write-Host "娴嬭瘯瀹屾垚!" -ForegroundColor Cyan 
