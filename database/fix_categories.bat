@echo off
echo 正在修复商品分类...

mysql -u root -p123456 lucky_mall -e "SELECT 'Current categories:' as info;"
mysql -u root -p123456 lucky_mall -e "SELECT * FROM categories ORDER BY id;"

echo 创建手机通讯分类...
mysql -u root -p123456 lucky_mall -e "INSERT IGNORE INTO categories (name, parent_id, level, sort_order, status) VALUES ('手机通讯', NULL, 1, 1, 1);"

echo 将小米14移动到手机分类...
mysql -u root -p123456 lucky_mall -e "UPDATE products p SET p.category_id = (SELECT c.id FROM categories c WHERE c.name IN ('Mobile Phone', '手机通讯') LIMIT 1) WHERE p.name = '小米14';"

echo 删除数码产品分类...
mysql -u root -p123456 lucky_mall -e "DELETE FROM categories WHERE name = '数码产品' AND id NOT IN (SELECT DISTINCT category_id FROM products WHERE category_id IS NOT NULL);"

echo 创建家用电器分类...
mysql -u root -p123456 lucky_mall -e "INSERT IGNORE INTO categories (name, parent_id, level, sort_order, status) VALUES ('家用电器', NULL, 1, 9, 1);"

echo 创建运动户外分类...
mysql -u root -p123456 lucky_mall -e "INSERT IGNORE INTO categories (name, parent_id, level, sort_order, status) VALUES ('运动户外', NULL, 1, 4, 1);"

echo 将Nike运动鞋移动到运动户外分类...
mysql -u root -p123456 lucky_mall -e "UPDATE products p SET p.category_id = (SELECT c.id FROM categories c WHERE c.name = '运动户外' LIMIT 1) WHERE p.name LIKE '%Nike%' OR p.name LIKE '%运动鞋%';"

echo 查看修复后的结果...
mysql -u root -p123456 lucky_mall -e "SELECT p.id, p.name, p.category_id, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY c.name, p.name;"

echo 分类修复完成！
pause 