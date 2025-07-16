-- 插入购物车测试数据
USE lucky_mall;

-- 首先插入测试用户
INSERT INTO users (id, username, password, email, phone) VALUES
(1, 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'test1@example.com', '13800138001'),
(2, 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'test2@example.com', '13800138002')
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- 为测试用户（ID=1）添加一些购物车数据
INSERT INTO cart_items (user_id, product_id, quantity) VALUES
(1, 1, 2),   -- iPhone 15 Pro x 2
(1, 6, 1),   -- MacBook Pro 16 x 1
(1, 11, 1),  -- AirPods Pro 2 x 1
(1, 17, 1);  -- Nike Air Force 1 x 1

-- 为测试用户（ID=2）添加一些购物车数据
INSERT INTO cart_items (user_id, product_id, quantity) VALUES
(2, 3, 1),   -- Xiaomi 14 Ultra x 1
(2, 12, 1),  -- Sony WH-1000XM5 x 1
(2, 22, 2);  -- ZARA Dress x 2

-- 验证插入的数据
SELECT 
    c.id, 
    c.user_id, 
    p.name as product_name, 
    c.quantity, 
    p.price,
    (c.quantity * p.price) as subtotal
FROM cart_items c
LEFT JOIN products p ON c.product_id = p.id
ORDER BY c.user_id, c.id;

SELECT 'Cart test data inserted successfully!' as message; 