-- 招财商城 - 完整分类修复脚本
-- 将所有商品精确分配到正确的5个分类中

USE lucky_mall;

-- 显示修复前的状态
SELECT '=== 修复前的分类分布 ===' as info;
SELECT p.category_id, c.name as category_name, COUNT(p.id) as count,
       GROUP_CONCAT(p.name ORDER BY p.name SEPARATOR '; ') as products
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
GROUP BY p.category_id, c.name 
ORDER BY p.category_id;

-- 1. 首先清理并重建分类结构
DELETE FROM categories;

INSERT INTO categories (id, name, parent_id, level, sort_order, status) VALUES
(1, '手机', NULL, 1, 1, 1),
(2, '电脑', NULL, 1, 2, 1),
(3, '数码配件', NULL, 1, 3, 1),
(4, '服装', NULL, 1, 4, 1),
(5, '家居生活', NULL, 1, 5, 1);

-- 2. 精确分类商品

-- 手机类商品 (ID: 1)
UPDATE products SET category_id = 1 WHERE name IN (
    'iPhone 15 Pro Max',
    'Huawei Mate 60 Pro',
    'OPPO Find X7 Pro',
    'vivo X100 Pro',
    '小米14',
    'Samsung Galaxy S24 Ultra'
) OR name LIKE '%手机%' OR name LIKE '%Phone%';

-- 电脑类商品 (ID: 2)
UPDATE products SET category_id = 2 WHERE name IN (
    'MacBook Pro 16',
    'Lenovo ThinkPad X1',
    'Dell XPS 13',
    'ASUS ZenBook Pro',
    'Microsoft Surface Pro'
) OR name LIKE '%MacBook%' OR name LIKE '%ThinkPad%' OR name LIKE '%电脑%' OR name LIKE '%笔记本%';

-- 数码配件类商品 (ID: 3)
UPDATE products SET category_id = 3 WHERE name IN (
    'AirPods Pro 2',
    'Sony WH-1000XM5',
    'iPad Pro 12.9',
    'Apple Watch Ultra',
    'Anker Power Bank',
    'Logitech MX Master 3S',
    'Bose Noise Cancelling Headphones',
    'JBL Flip 6 Speaker',
    'RAVPower Wireless Charger',
    'SanDisk Ultra USB-C Flash Drive'
) OR name LIKE '%AirPods%' OR name LIKE '%耳机%' OR name LIKE '%音箱%' OR name LIKE '%充电%' OR name LIKE '%iPad%' OR name LIKE '%Watch%';

-- 服装类商品 (ID: 4)
UPDATE products SET category_id = 4 WHERE name IN (
    'Nike Air Max 270',
    'Adidas Ultraboost 22',
    'Uniqlo Down Jacket',
    'Levis Jeans',
    'Champion Hoodie',
    'ZARA Dress',
    'Converse Chuck Taylor All Star',
    'Puma RS-X Sneakers',
    'H&M Basic T-Shirt',
    'Gap Straight Jeans'
) OR name LIKE '%Nike%' OR name LIKE '%Adidas%' OR name LIKE '%Uniqlo%' OR name LIKE '%Levis%' OR name LIKE '%Champion%' OR name LIKE '%ZARA%' OR name LIKE '%衣服%' OR name LIKE '%服装%' OR name LIKE '%鞋%' OR name LIKE '%T恤%' OR name LIKE '%Jeans%' OR name LIKE '%Hoodie%' OR name LIKE '%Dress%';

-- 家居生活类商品 (ID: 5)
UPDATE products SET category_id = 5 WHERE name IN (
    'Dyson V15 Detect',
    'Nespresso Vertuo Next',
    'KitchenAid Stand Mixer',
    'Vitamix A3500 Blender',
    'Le Creuset Dutch Oven',
    'Instant Pot Duo Crisp',
    'Philips Sonicare Toothbrush',
    'Shark Navigator Vacuum',
    'Cuisinart Coffee Maker',
    'OXO Good Grips Knife Set'
) OR name LIKE '%Dyson%' OR name LIKE '%洗衣机%' OR name LIKE '%空调%' OR name LIKE '%冰箱%' OR name LIKE '%吸尘器%' OR name LIKE '%家居%' OR name LIKE '%厨具%';

-- 3. 处理剩余未分类的商品（分配到数码配件）
UPDATE products SET category_id = 3 WHERE category_id IS NULL OR category_id NOT IN (1,2,3,4,5);

-- 显示修复后的状态
SELECT '=== 修复后的分类分布 ===' as info;
SELECT p.category_id, c.name as category_name, COUNT(p.id) as count,
       GROUP_CONCAT(p.name ORDER BY p.name SEPARATOR '; ') as products
FROM products p 
LEFT JOIN categories c ON p.category_id = c.id 
GROUP BY p.category_id, c.name 
ORDER BY p.category_id;

SELECT '=== 修复完成 ===' as result; 