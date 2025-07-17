-- 手动执行：精简分类为5个主要分类
-- 请在MySQL命令行中逐步执行以下命令

USE lucky_mall;

-- 第一步：删除所有现有分类
DELETE FROM categories;

-- 第二步：创建新的5个分类
INSERT INTO categories (id, name, parent_id, level, sort_order, status) VALUES
(1, '手机', NULL, 1, 1, 1),
(2, '电脑', NULL, 1, 2, 1),
(3, '其他数码产品', NULL, 1, 3, 1),
(4, '服装', NULL, 1, 4, 1),
(5, '家居生活', NULL, 1, 5, 1);

-- 第三步：重新分配商品到新分类

-- 手机类商品 -> 分类1
UPDATE products SET category_id = 1 WHERE 
name LIKE '%手机%' OR name LIKE '%iPhone%' OR name LIKE '%小米%' OR 
name LIKE '%华为%' OR name LIKE '%Huawei%' OR name LIKE '%Phone%';

-- 电脑类商品 -> 分类2  
UPDATE products SET category_id = 2 WHERE 
name LIKE '%电脑%' OR name LIKE '%笔记本%' OR name LIKE '%MacBook%' OR 
name LIKE '%ThinkPad%' OR name LIKE '%Computer%';

-- 其他数码产品 -> 分类3
UPDATE products SET category_id = 3 WHERE 
name LIKE '%AirPods%' OR name LIKE '%耳机%' OR name LIKE '%音箱%' OR 
name LIKE '%充电%' OR name LIKE '%数据线%' OR name LIKE '%Accessories%' OR name LIKE '%Digital%';

-- 服装类商品 -> 分类4
UPDATE products SET category_id = 4 WHERE 
name LIKE '%衣服%' OR name LIKE '%T恤%' OR name LIKE '%衬衫%' OR 
name LIKE '%外套%' OR name LIKE '%裙子%' OR name LIKE '%Fashion%' OR name LIKE '%服装%';

-- 鞋帽类商品 -> 分类5
UPDATE products SET category_id = 5 WHERE 
name LIKE '%鞋%' OR name LIKE '%帽子%' OR name LIKE '%Nike%' OR 
name LIKE '%运动鞋%' OR name LIKE '%凉鞋%' OR name LIKE '%靴%';

-- 家居生活类商品 -> 分类6
UPDATE products SET category_id = 6 WHERE 
name LIKE '%沙发%' OR name LIKE '%桌子%' OR name LIKE '%椅子%' OR 
name LIKE '%床%' OR name LIKE '%家具%' OR name LIKE '%装饰%' OR 
name LIKE '%厨具%' OR name LIKE '%家居%';

-- 第四步：将未分类的商品分配到"其他数码产品"
UPDATE products SET category_id = 3 WHERE category_id IS NULL OR category_id NOT IN (1,2,3,4,5,6);

-- 第五步：查看结果
SELECT '新的分类列表:' as info;
SELECT * FROM categories ORDER BY sort_order;

SELECT '商品分类情况:' as info;
SELECT p.id, p.name, p.category_id, c.name as category_name 
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
ORDER BY c.sort_order, p.name; 