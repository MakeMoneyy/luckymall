-- 招财商城 - 30件精选商品数据更新脚本
-- 使用数据库
USE lucky_mall;

-- 清空现有商品数据，重新插入30件精选商品
DELETE FROM products;
ALTER TABLE products AUTO_INCREMENT = 1;

-- 插入30件精选商品，确保图片与商品匹配
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url, sales_count, status) VALUES

-- 手机通讯类商品 (5件)
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB 钛原色 A17 Pro芯片', 8999.00, 50, 6, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&h=400&fit=crop&crop=center&q=80', 128, 1),
('华为 Mate 60 Pro', '华为 Mate 60 Pro 512GB 雅黑色 鸿蒙4.0', 6999.00, 30, 6, 'https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=400&h=400&fit=crop&crop=center&q=80', 89, 1),
('小米 14 Ultra', '小米 14 Ultra 徕卡影像 16GB+512GB 钛金属', 5999.00, 40, 6, 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=400&fit=crop&crop=center&q=80', 156, 1),
('OPPO Find X7 Pro', 'OPPO Find X7 Pro 哈苏影像 16GB+512GB', 5499.00, 35, 6, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop&crop=center&q=80', 67, 1),
('vivo X100 Pro', 'vivo X100 Pro 蔡司影像 16GB+512GB 星迹蓝', 4999.00, 45, 6, 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=400&h=400&fit=crop&crop=center&q=80', 78, 1),

-- 电脑办公类商品 (5件)
('MacBook Pro 16', 'Apple MacBook Pro 16英寸 M3 Pro芯片 18GB+512GB', 19999.00, 20, 7, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop&crop=center&q=80', 45, 1),
('联想ThinkPad X1', '联想ThinkPad X1 Carbon Gen11 14英寸 i7-1365U', 12999.00, 25, 7, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop&crop=center&q=80', 32, 1),
('戴尔XPS 13', '戴尔XPS 13 Plus 13.4英寸 i7-1360P 16GB+1TB', 10999.00, 30, 7, 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop&crop=center&q=80', 28, 1),
('华硕ZenBook Pro', '华硕ZenBook Pro 15 OLED创作本 i9-13900H RTX4060', 13999.00, 15, 7, 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400&h=400&fit=crop&crop=center&q=80', 21, 1),
('微软Surface Pro', '微软Surface Pro 9 13英寸二合一平板电脑 i7', 8999.00, 35, 7, 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=400&h=400&fit=crop&crop=center&q=80', 38, 1),

-- 数码配件类商品 (6件)
('AirPods Pro 2', 'Apple AirPods Pro 第二代 主动降噪 空间音频', 1899.00, 80, 8, 'https://images.unsplash.com/photo-1606400082777-ef05f3c5cde4?w=400&h=400&fit=crop&crop=center&q=80', 189, 1),
('Sony WH-1000XM5', 'Sony WH-1000XM5 头戴式无线降噪耳机 黑色', 2399.00, 60, 8, 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=400&h=400&fit=crop&crop=center&q=80', 145, 1),
('iPad Pro 12.9', 'Apple iPad Pro 12.9英寸 M2芯片 128GB WLAN版', 8499.00, 40, 8, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop&crop=center&q=80', 67, 1),
('Apple Watch Ultra', 'Apple Watch Ultra 2 49毫米钛金属表壳 运动表带', 6399.00, 50, 8, 'https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400&h=400&fit=crop&crop=center&q=80', 89, 1),
('Anker充电宝', 'Anker 20000mAh 65W超级充电宝 双向快充', 399.00, 200, 8, 'https://images.unsplash.com/photo-1609592853103-b27a0e0138bf?w=400&h=400&fit=crop&crop=center&q=80', 567, 1),
('罗技MX Master 3S', '罗技MX Master 3S 无线鼠标 办公神器', 699.00, 120, 8, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=400&fit=crop&crop=center&q=80', 234, 1),

-- 男装类商品 (5件)
('Nike Air Force 1', 'Nike Air Force 1 07 经典小白鞋 男女同款', 899.00, 150, 9, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400&h=400&fit=crop&crop=center&q=80', 289, 1),
('Adidas Stan Smith', 'Adidas Stan Smith 绿尾小白鞋 经典板鞋', 699.00, 180, 9, 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5a?w=400&h=400&fit=crop&crop=center&q=80', 234, 1),
('优衣库羽绒服', '优衣库男装高级轻型羽绒服 多色可选', 599.00, 120, 9, 'https://images.unsplash.com/photo-1551928134-c4c5d4abaa2c?w=400&h=400&fit=crop&crop=center&q=80', 178, 1),
('Levis牛仔裤', 'Levis 511修身牛仔裤 经典蓝色水洗', 599.00, 100, 9, 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=400&fit=crop&crop=center&q=80', 156, 1),
('Champion卫衣', 'Champion经典logo连帽卫衣 纯棉加绒', 399.00, 200, 9, 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=400&fit=crop&crop=center&q=80', 234, 1),

-- 女装类商品 (5件)
('ZARA连衣裙', 'ZARA女装春夏新款印花连衣裙 法式复古', 499.00, 100, 10, 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=400&h=400&fit=crop&crop=center&q=80', 167, 1),
('HM西装外套', 'HM女装修身西装外套 职业通勤', 399.00, 80, 10, 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=400&h=400&fit=crop&crop=center&q=80', 98, 1),
('Vero Moda毛衣', 'Vero Moda纯羊毛V领毛衣 温柔风', 399.00, 120, 10, 'https://images.unsplash.com/photo-1571513722275-4b8c9ee8ba5b?w=400&h=400&fit=crop&crop=center&q=80', 156, 1),
('COS半身裙', 'COS高腰A字半身裙 极简设计', 599.00, 90, 10, 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=400&h=400&fit=crop&crop=center&q=80', 123, 1),
('Uniqlo T恤', 'Uniqlo AIRism凉感T恤 夏季必备', 99.00, 300, 10, 'https://images.unsplash.com/photo-1556821840-3a9fbc8e7696?w=400&h=400&fit=crop&crop=center&q=80', 456, 1),

-- 家居用品类商品 (4件) - 使用分类ID 12 (厨房用品)
('戴森吸尘器V15', '戴森V15 Detect无绳吸尘器 激光探测科技', 4999.00, 40, 12, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400&h=400&fit=crop&crop=center&q=80', 89, 1),
('小米扫地机器人', '小米扫地机器人S10+ 自动集尘 激光导航', 2999.00, 60, 12, 'https://images.unsplash.com/photo-1600298881974-6be191ceeda1?w=400&h=400&fit=crop&crop=center&q=80', 134, 1),
('美的洗衣机', '美的10KG变频滚筒洗衣机 除菌洗', 2399.00, 30, 12, 'https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=400&h=400&fit=crop&crop=center&q=80', 67, 1),
('格力空调', '格力1.5匹变频冷暖空调 一级能效', 3299.00, 25, 12, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400&h=400&fit=crop&crop=center&q=80', 78, 1);

-- 验证插入的商品数量
SELECT 
    c.name as category_name,
    COUNT(p.id) as product_count,
    MIN(p.price) as min_price,
    MAX(p.price) as max_price,
    AVG(p.price) as avg_price
FROM categories c
LEFT JOIN products p ON c.id = p.category_id
WHERE c.id IN (6, 7, 8, 9, 10, 12)
GROUP BY c.id, c.name
ORDER BY product_count DESC;

-- 显示总商品数量
SELECT COUNT(*) as total_products FROM products WHERE status = 1;

COMMIT; 