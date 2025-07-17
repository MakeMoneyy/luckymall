@echo off
echo ========================================
echo 招财商城 - 分类精简工具
echo ========================================
echo.
echo 正在精简商品分类为6个主要分类...
echo 1. 手机
echo 2. 电脑  
echo 3. 其他数码产品
echo 4. 服装
echo 5. 鞋帽
echo 6. 家居生活
echo.

pause

echo 开始执行分类精简...

echo [1/6] 删除现有分类...
mysql -u root -p123456 lucky_mall -e "DELETE FROM categories;"

echo [2/6] 创建新分类...
mysql -u root -p123456 lucky_mall -e "INSERT INTO categories (id, name, parent_id, level, sort_order, status) VALUES (1, '手机', NULL, 1, 1, 1), (2, '电脑', NULL, 1, 2, 1), (3, '其他数码产品', NULL, 1, 3, 1), (4, '服装', NULL, 1, 4, 1), (5, '鞋帽', NULL, 1, 5, 1), (6, '家居生活', NULL, 1, 6, 1);"

echo [3/6] 分配手机类商品...
mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 1 WHERE name LIKE '%%手机%%' OR name LIKE '%%iPhone%%' OR name LIKE '%%小米%%' OR name LIKE '%%华为%%' OR name LIKE '%%Huawei%%' OR name LIKE '%%Phone%%';"

echo [4/6] 分配电脑类商品...
mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 2 WHERE name LIKE '%%电脑%%' OR name LIKE '%%笔记本%%' OR name LIKE '%%MacBook%%' OR name LIKE '%%ThinkPad%%' OR name LIKE '%%Computer%%';"

echo [5/6] 分配其他数码产品...
mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 3 WHERE name LIKE '%%AirPods%%' OR name LIKE '%%耳机%%' OR name LIKE '%%音箱%%' OR name LIKE '%%充电%%' OR name LIKE '%%数据线%%' OR name LIKE '%%Accessories%%' OR name LIKE '%%Digital%%';"

echo [6/6] 分配服装鞋帽家居类商品...
mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 4 WHERE name LIKE '%%衣服%%' OR name LIKE '%%T恤%%' OR name LIKE '%%衬衫%%' OR name LIKE '%%外套%%' OR name LIKE '%%裙子%%' OR name LIKE '%%Fashion%%' OR name LIKE '%%服装%%';"
mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 5 WHERE name LIKE '%%鞋%%' OR name LIKE '%%帽子%%' OR name LIKE '%%Nike%%' OR name LIKE '%%运动鞋%%' OR name LIKE '%%凉鞋%%' OR name LIKE '%%靴%%';"
mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 6 WHERE name LIKE '%%沙发%%' OR name LIKE '%%桌子%%' OR name LIKE '%%椅子%%' OR name LIKE '%%床%%' OR name LIKE '%%家具%%' OR name LIKE '%%装饰%%' OR name LIKE '%%厨具%%' OR name LIKE '%%家居%%';"

echo 处理未分类商品...
mysql -u root -p123456 lucky_mall -e "UPDATE products SET category_id = 3 WHERE category_id IS NULL OR category_id NOT IN (1,2,3,4,5,6);"

echo.
echo ========================================
echo 分类精简完成！查看结果...
echo ========================================

mysql -u root -p123456 lucky_mall -e "SELECT '新的分类列表:' as info; SELECT * FROM categories ORDER BY sort_order;"

mysql -u root -p123456 lucky_mall -e "SELECT '商品分类统计:' as info; SELECT c.name as category_name, COUNT(p.id) as product_count FROM categories c LEFT JOIN products p ON c.id = p.category_id GROUP BY c.id, c.name ORDER BY c.sort_order;"

echo.
echo 分类精简成功！现在您的商城有6个清晰的主要分类。
echo 请刷新前端页面查看效果。
echo.
pause 