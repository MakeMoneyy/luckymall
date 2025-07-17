# 修复商品分类的PowerShell脚本

Write-Host "正在修复商品分类..." -ForegroundColor Green

# 1. 将小米14移动到Mobile Phone分类(id=6)
Write-Host "将小米14移动到手机分类..." -ForegroundColor Yellow
& mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 6 WHERE name = '小米14';"

# 2. 删除数码产品分类
Write-Host "删除数码产品分类..." -ForegroundColor Yellow
& mysql -u root -p123456 lucky_mall -e "DELETE FROM categories WHERE name = '数码产品';"

# 3. 查看修复后的结果
Write-Host "查看修复后的商品分类情况:" -ForegroundColor Green
& mysql -u root -p123456 lucky_mall -e "SELECT p.id, p.name, p.category_id, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY c.name, p.name;"

Write-Host "分类修复完成！" -ForegroundColor Green 