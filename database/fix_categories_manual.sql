-- 手动执行：修复商品分类错误
-- 请在MySQL命令行中逐步执行以下命令

USE lucky_mall;

-- 第一步：创建5个正确分类（如果不存在）
INSERT IGNORE INTO categories (id, name, parent_id, level, sort_order, status) VALUES
(1, '手机', NULL, 1, 1, 1),
(2, '电脑', NULL, 1, 2, 1),
(3, '其他数码产品', NULL, 1, 3, 1),
(4, '服装', NULL, 1, 4, 1),
(5, '家居生活', NULL, 1, 5, 1);

-- 第二步：修复手机分类（从其他分类移到手机分类）
UPDATE products SET category_id = 1 WHERE 
name LIKE '%手机%' OR name LIKE '%iPhone%' OR name LIKE '%小米%' OR 
name LIKE '%华为%' OR name LIKE '%OPPO%' OR name LIKE '%vivo%' OR 
name LIKE '%Phone%' OR name LIKE '%Mate%' OR name LIKE '%Find%';

-- 第三步：修复服装分类（包括鞋帽）
UPDATE products SET category_id = 4 WHERE 
name LIKE '%衣服%' OR name LIKE '%T恤%' OR name LIKE '%衬衫%' OR 
name LIKE '%外套%' OR name LIKE '%裙子%' OR name LIKE '%服装%' OR
name LIKE '%鞋%' OR name LIKE '%帽子%' OR name LIKE '%Nike%' OR 
name LIKE '%运动鞋%' OR name LIKE '%Adidas%';

-- 第四步：修复家居生活分类（包括家电）
UPDATE products SET category_id = 5 WHERE 
name LIKE '%洗衣机%' OR name LIKE '%空调%' OR name LIKE '%冰箱%' OR 
name LIKE '%电视%' OR name LIKE '%吸尘器%' OR name LIKE '%扫地机器人%' OR
name LIKE '%沙发%' OR name LIKE '%家具%' OR name LIKE '%家居%' OR
name LIKE '%戴森%' OR name LIKE '%美的%' OR name LIKE '%格力%';

-- 第五步：修复电脑分类
UPDATE products SET category_id = 2 WHERE 
name LIKE '%电脑%' OR name LIKE '%笔记本%' OR name LIKE '%MacBook%' OR 
name LIKE '%ThinkPad%' OR name LIKE '%Computer%';

-- 第六步：其他数码产品分类
UPDATE products SET category_id = 3 WHERE 
name LIKE '%AirPods%' OR name LIKE '%耳机%' OR name LIKE '%音箱%' OR 
name LIKE '%充电%' OR name LIKE '%数据线%';

-- 第七步：清理错误分类
DELETE FROM categories WHERE name IN ('数码产品', '服装鞋帽') AND id NOT IN (1,2,3,4,5);

-- 第八步：查看修复结果
SELECT '修复后的分类:' as info;
SELECT id, name FROM categories WHERE id IN (1,2,3,4,5) ORDER BY id;

SELECT '各分类商品数量:' as info;
SELECT c.name as category_name, COUNT(p.id) as product_count
FROM categories c
LEFT JOIN products p ON c.id = p.category_id
WHERE c.id IN (1,2,3,4,5)
GROUP BY c.id, c.name
ORDER BY c.id; 