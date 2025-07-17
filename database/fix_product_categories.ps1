# 招财商城 - 商品分类错误修复脚本 (PowerShell版本)

Write-Host "========================================" -ForegroundColor Green
Write-Host "招财商城 - 商品分类错误修复工具" -ForegroundColor Green  
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "准备修复以下问题：" -ForegroundColor Yellow
Write-Host "1. 服装鞋帽类是空的" -ForegroundColor White
Write-Host "2. 服装鞋帽和洗衣机空调都在其他数码产品分类" -ForegroundColor White
Write-Host "3. 有些手机在家居生活分类" -ForegroundColor White
Write-Host ""

Write-Host "修复后将有5个清晰的分类：" -ForegroundColor Yellow
Write-Host "1. 手机" -ForegroundColor White
Write-Host "2. 电脑" -ForegroundColor White  
Write-Host "3. 其他数码产品" -ForegroundColor White
Write-Host "4. 服装" -ForegroundColor White
Write-Host "5. 家居生活" -ForegroundColor White
Write-Host ""

Read-Host "按任意键开始修复"

Write-Host "开始执行分类修复..." -ForegroundColor Green

try {
    # 执行SQL修复脚本
    Write-Host "正在执行数据库修复..." -ForegroundColor Yellow
    & mysql -u root -p123456 lucky_mall -e "source database/fix_product_categories.sql"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "商品分类修复完成！" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        
        Write-Host "现在您可以：" -ForegroundColor Yellow
        Write-Host "1. 刷新前端页面查看效果" -ForegroundColor White
        Write-Host "2. 检查各分类下的商品是否正确" -ForegroundColor White
        Write-Host "3. 验证主页分类区域显示" -ForegroundColor White
        Write-Host ""
        
        Write-Host "查看修复结果..." -ForegroundColor Yellow
        & mysql -u root -p123456 lucky_mall -e "SELECT '各分类商品数量:' as info; SELECT c.name as category_name, COUNT(p.id) as product_count FROM categories c LEFT JOIN products p ON c.id = p.category_id WHERE c.id IN (1,2,3,4,5) GROUP BY c.id, c.name ORDER BY c.id;"
        
    } else {
        Write-Host "修复过程中出现错误，请检查MySQL连接和密码。" -ForegroundColor Red
    }
}
catch {
    Write-Host "执行修复脚本时出现错误：$_" -ForegroundColor Red
}

Write-Host ""
Read-Host "按任意键退出" 