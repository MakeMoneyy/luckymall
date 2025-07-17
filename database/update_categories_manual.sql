-- 请在MySQL命令行或其他MySQL客户端中执行以下SQL语句

USE lucky_mall;

-- 第一步：查看当前商品和分类情况
SELECT '当前商品分类情况:' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
ORDER BY p.category_id;

-- 第二步：将小米14移动到Mobile Phone分类
UPDATE products 
SET category_id = 6 
WHERE name = '小米14';

-- 第三步：删除数码产品分类（如果没有商品使用）
DELETE FROM categories 
WHERE name = '数码产品' AND id = 11;

-- 第四步：添加新的分类（如果需要）
INSERT IGNORE INTO categories (name, parent_id, level, sort_order, status) 
VALUES 
('手机通讯', NULL, 1, 1, 1),
('家用电器', NULL, 1, 9, 1),
('运动户外', NULL, 1, 4, 1);

-- 第五步：修复Nike运动鞋分类
UPDATE products 
SET category_id = (SELECT id FROM categories WHERE name = '运动户外' LIMIT 1) 
WHERE name LIKE '%Nike%' OR name LIKE '%运动鞋%';

-- 第六步：查看修复后的结果
SELECT '修复后的商品分类情况:' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
ORDER BY c.name, p.name;

SELECT '所有分类:' as info;
SELECT * FROM categories ORDER BY sort_order, id; 