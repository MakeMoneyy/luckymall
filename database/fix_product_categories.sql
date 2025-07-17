-- 招财商城 - 商品分类错误修复脚本
-- 修复问题：
-- 1. 服装鞋帽类是空的
-- 2. 服装鞋帽和洗衣机空调都在其他数码产品分类
-- 3. 有些手机在家居生活分类

USE lucky_mall;

-- 查看当前分类和商品分布情况
SELECT '=== 当前分类列表 ===' as info;
SELECT id, name, parent_id FROM categories ORDER BY id;

SELECT '=== 当前商品分类分布 ===' as info;
SELECT 
    p.category_id,
    c.name as category_name,
    COUNT(p.id) as product_count,
    GROUP_CONCAT(p.name SEPARATOR ', ') as products
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
GROUP BY p.category_id, c.name 
ORDER BY p.category_id;

-- 第一步：创建正确的分类结构（如果不存在）
INSERT IGNORE INTO categories (id, name, parent_id, level, sort_order, status) VALUES
(1, '手机', NULL, 1, 1, 1),
(2, '电脑', NULL, 1, 2, 1),
(3, '其他数码产品', NULL, 1, 3, 1),
(4, '服装', NULL, 1, 4, 1),
(5, '家居生活', NULL, 1, 5, 1);

-- 第二步：修复手机分类 - 将错误分类在家居生活中的手机移回手机分类
UPDATE products SET category_id = 1 WHERE 
    (name LIKE '%手机%' OR 
     name LIKE '%iPhone%' OR 
     name LIKE '%小米%14%' OR 
     name LIKE '%华为%' OR 
     name LIKE '%Huawei%' OR 
     name LIKE '%OPPO%' OR 
     name LIKE '%vivo%' OR 
     name LIKE '%Phone%' OR
     name LIKE '%Mate%' OR
     name LIKE '%Find%') 
    AND category_id != 1;

-- 第三步：修复服装分类 - 将错误分类在其他数码产品中的服装移到服装分类
UPDATE products SET category_id = 4 WHERE 
    (name LIKE '%衣服%' OR 
     name LIKE '%T恤%' OR 
     name LIKE '%衬衫%' OR 
     name LIKE '%外套%' OR 
     name LIKE '%裙子%' OR 
     name LIKE '%Fashion%' OR 
     name LIKE '%服装%' OR
     name LIKE '%鞋%' OR 
     name LIKE '%帽子%' OR 
     name LIKE '%Nike%' OR 
     name LIKE '%运动鞋%' OR 
     name LIKE '%凉鞋%' OR 
     name LIKE '%靴%' OR
     name LIKE '%Adidas%') 
    AND category_id != 4;

-- 第四步：修复家居生活分类 - 将洗衣机、空调等家电移到家居生活分类
UPDATE products SET category_id = 5 WHERE 
    (name LIKE '%洗衣机%' OR 
     name LIKE '%空调%' OR 
     name LIKE '%冰箱%' OR 
     name LIKE '%电视%' OR 
     name LIKE '%吸尘器%' OR 
     name LIKE '%扫地机器人%' OR
     name LIKE '%沙发%' OR 
     name LIKE '%桌子%' OR 
     name LIKE '%椅子%' OR 
     name LIKE '%床%' OR 
     name LIKE '%家具%' OR 
     name LIKE '%装饰%' OR 
     name LIKE '%厨具%' OR 
     name LIKE '%家居%' OR
     name LIKE '%戴森%' OR
     name LIKE '%美的%' OR
     name LIKE '%格力%') 
    AND category_id != 5;

-- 第五步：修复电脑分类
UPDATE products SET category_id = 2 WHERE 
    (name LIKE '%电脑%' OR 
     name LIKE '%笔记本%' OR 
     name LIKE '%MacBook%' OR 
     name LIKE '%ThinkPad%' OR 
     name LIKE '%Computer%' OR
     name LIKE '%laptop%') 
    AND category_id != 2;

-- 第六步：将剩余的数码配件商品分配到其他数码产品
UPDATE products SET category_id = 3 WHERE 
    (name LIKE '%AirPods%' OR 
     name LIKE '%耳机%' OR 
     name LIKE '%音箱%' OR 
     name LIKE '%充电%' OR 
     name LIKE '%数据线%' OR 
     name LIKE '%Accessories%' OR 
     name LIKE '%Digital%') 
    AND category_id != 3;

-- 第七步：删除可能存在的错误分类（如果没有商品使用）
DELETE FROM categories WHERE name IN ('数码产品', '服装鞋帽') AND id NOT IN (1,2,3,4,5);

-- 第八步：查看修复后的结果
SELECT '=== 修复后的分类列表 ===' as info;
SELECT id, name, parent_id FROM categories WHERE id IN (1,2,3,4,5) ORDER BY id;

SELECT '=== 修复后的商品分类分布 ===' as info;
SELECT 
    p.category_id,
    c.name as category_name,
    COUNT(p.id) as product_count,
    GROUP_CONCAT(p.name SEPARATOR ', ') as products
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
WHERE p.category_id IN (1,2,3,4,5)
GROUP BY p.category_id, c.name 
ORDER BY p.category_id;

-- 第九步：统计各分类商品数量
SELECT '=== 各分类商品数量统计 ===' as info;
SELECT 
    c.id as category_id,
    c.name as category_name,
    COUNT(p.id) as product_count
FROM categories c
LEFT JOIN products p ON c.id = p.category_id
WHERE c.id IN (1,2,3,4,5)
GROUP BY c.id, c.name
ORDER BY c.id;

-- 第十步：检查是否还有商品没有正确分类
SELECT '=== 未正确分类的商品 ===' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
WHERE p.category_id NOT IN (1,2,3,4,5) OR p.category_id IS NULL; 