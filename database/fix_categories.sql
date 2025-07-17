USE lucky_mall;

-- 查看当前分类和商品情况
SELECT 'Current categories:' as info;
SELECT * FROM categories ORDER BY id;

SELECT 'Current products with categories:' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
ORDER BY p.category_id;

-- 1. 将小米14从数码产品分类(id=11)移动到手机分类
-- 首先确保手机分类存在，如果不存在则创建
INSERT IGNORE INTO categories (name, parent_id, level, sort_order, status) 
VALUES ('手机通讯', NULL, 1, 1, 1);

-- 获取手机分类的ID（可能是6或者新创建的）
SET @phone_category_id = (SELECT id FROM categories WHERE name IN ('Mobile Phone', '手机通讯') LIMIT 1);

-- 将小米14移动到手机分类
UPDATE products 
SET category_id = @phone_category_id 
WHERE name = '小米14' AND category_id = 11;

-- 2. 删除数码产品分类(id=11)，但先确保没有其他商品使用这个分类
-- 检查是否还有其他商品使用数码产品分类
SELECT 'Products still in 数码产品 category:' as info;
SELECT * FROM products WHERE category_id = 11;

-- 如果没有商品使用数码产品分类，则删除它
DELETE FROM categories WHERE id = 11 AND name = '数码产品';

-- 3. 修正其他可能的分类错误
-- 如果有空调商品在服装鞋帽分类中，将其移动到家电分类
-- 首先创建家电分类（如果不存在）
INSERT IGNORE INTO categories (name, parent_id, level, sort_order, status) 
VALUES ('家用电器', NULL, 1, 9, 1);

SET @appliance_category_id = (SELECT id FROM categories WHERE name = '家用电器' LIMIT 1);

-- 将空调类商品移动到家电分类
UPDATE products 
SET category_id = @appliance_category_id 
WHERE (name LIKE '%空调%' OR name LIKE '%冰箱%' OR name LIKE '%洗衣机%' OR name LIKE '%电视%') 
AND category_id = 12; -- 服装鞋帽分类ID

-- 4. 确保Nike运动鞋在正确的分类中
-- 创建运动户外分类（如果不存在）
INSERT IGNORE INTO categories (name, parent_id, level, sort_order, status) 
VALUES ('运动户外', NULL, 1, 4, 1);

SET @sports_category_id = (SELECT id FROM categories WHERE name = '运动户外' LIMIT 1);

-- 将Nike运动鞋移动到运动户外分类
UPDATE products 
SET category_id = @sports_category_id 
WHERE name LIKE '%Nike%' OR name LIKE '%运动鞋%';

-- 最终查看修复后的结果
SELECT 'Final categories:' as info;
SELECT * FROM categories ORDER BY sort_order, id;

SELECT 'Final products with categories:' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
ORDER BY c.name, p.name; 