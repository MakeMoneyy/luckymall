-- 精简分类为5个主要分类
USE lucky_mall;

-- 查看当前分类和商品情况
SELECT '当前分类情况:' as info;
SELECT * FROM categories ORDER BY id;

SELECT '当前商品分类情况:' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
ORDER BY p.category_id;

-- 1. 删除所有现有分类（先处理商品关联）
-- 备份当前商品分类关系
CREATE TEMPORARY TABLE temp_products_backup AS 
SELECT id, name, category_id FROM products;

-- 2. 创建新的5个主要分类
DELETE FROM categories;

INSERT INTO categories (id, name, parent_id, level, sort_order, status) VALUES
(1, '手机', NULL, 1, 1, 1),
(2, '电脑', NULL, 1, 2, 1),
(3, '其他数码产品', NULL, 1, 3, 1),
(4, '服装', NULL, 1, 4, 1),
(5, '鞋帽', NULL, 1, 5, 1),
(6, '家居生活', NULL, 1, 6, 1);

-- 3. 重新分配商品到新分类
-- 手机类商品
UPDATE products SET category_id = 1 WHERE 
name LIKE '%手机%' OR 
name LIKE '%iPhone%' OR 
name LIKE '%小米%' OR 
name LIKE '%华为%' OR 
name LIKE '%Huawei%' OR
name LIKE '%Phone%';

-- 电脑类商品
UPDATE products SET category_id = 2 WHERE 
name LIKE '%电脑%' OR 
name LIKE '%笔记本%' OR 
name LIKE '%MacBook%' OR 
name LIKE '%ThinkPad%' OR 
name LIKE '%Computer%' OR
name LIKE '%laptop%';

-- 其他数码产品
UPDATE products SET category_id = 3 WHERE 
name LIKE '%AirPods%' OR 
name LIKE '%耳机%' OR 
name LIKE '%音箱%' OR 
name LIKE '%充电%' OR 
name LIKE '%数据线%' OR
name LIKE '%Accessories%' OR
name LIKE '%Digital%';

-- 服装类商品
UPDATE products SET category_id = 4 WHERE 
name LIKE '%衣服%' OR 
name LIKE '%T恤%' OR 
name LIKE '%衬衫%' OR 
name LIKE '%外套%' OR 
name LIKE '%裙子%' OR
name LIKE '%Fashion%' OR
name LIKE '%服装%';

-- 鞋帽类商品
UPDATE products SET category_id = 5 WHERE 
name LIKE '%鞋%' OR 
name LIKE '%帽子%' OR 
name LIKE '%Nike%' OR 
name LIKE '%运动鞋%' OR 
name LIKE '%凉鞋%' OR
name LIKE '%靴%';

-- 家居生活类商品
UPDATE products SET category_id = 6 WHERE 
name LIKE '%沙发%' OR 
name LIKE '%桌子%' OR 
name LIKE '%椅子%' OR 
name LIKE '%床%' OR 
name LIKE '%家具%' OR
name LIKE '%装饰%' OR
name LIKE '%厨具%' OR
name LIKE '%家居%';

-- 对于没有匹配到的商品，分配到"其他数码产品"
UPDATE products SET category_id = 3 WHERE category_id IS NULL OR category_id NOT IN (1,2,3,4,5,6);

-- 4. 查看整理后的结果
SELECT '整理后的分类:' as info;
SELECT * FROM categories ORDER BY sort_order;

SELECT '整理后的商品分类情况:' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
ORDER BY c.sort_order, p.name;

-- 统计每个分类的商品数量
SELECT '各分类商品数量统计:' as info;
SELECT c.name as category_name, COUNT(p.id) as product_count
FROM categories c
LEFT JOIN products p ON c.id = p.category_id
GROUP BY c.id, c.name
ORDER BY c.sort_order; 