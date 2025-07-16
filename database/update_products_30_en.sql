-- Lucky Mall - 30 Selected Products Update Script
-- Use database
USE lucky_mall;

-- Clear existing product data and insert 30 selected products
DELETE FROM products;
ALTER TABLE products AUTO_INCREMENT = 1;

-- Insert 30 selected products with matching images
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url, sales_count, status) VALUES

-- Mobile Phone Category (5 items)
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB Titanium A17 Pro Chip', 8999.00, 50, 6, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&h=400&fit=crop&crop=center&q=80', 128, 1),
('Huawei Mate 60 Pro', 'Huawei Mate 60 Pro 512GB Black HarmonyOS 4.0', 6999.00, 30, 6, 'https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=400&h=400&fit=crop&crop=center&q=80', 89, 1),
('Xiaomi 14 Ultra', 'Xiaomi 14 Ultra Leica Camera 16GB+512GB Titanium', 5999.00, 40, 6, 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=400&fit=crop&crop=center&q=80', 156, 1),
('OPPO Find X7 Pro', 'OPPO Find X7 Pro Hasselblad Camera 16GB+512GB', 5499.00, 35, 6, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop&crop=center&q=80', 67, 1),
('vivo X100 Pro', 'vivo X100 Pro Zeiss Camera 16GB+512GB Blue', 4999.00, 45, 6, 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=400&h=400&fit=crop&crop=center&q=80', 78, 1),

-- Computer Category (5 items)
('MacBook Pro 16', 'Apple MacBook Pro 16-inch M3 Pro Chip 18GB+512GB', 19999.00, 20, 7, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop&crop=center&q=80', 45, 1),
('Lenovo ThinkPad X1', 'Lenovo ThinkPad X1 Carbon Gen11 14-inch i7-1365U', 12999.00, 25, 7, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop&crop=center&q=80', 32, 1),
('Dell XPS 13', 'Dell XPS 13 Plus 13.4-inch i7-1360P 16GB+1TB', 10999.00, 30, 7, 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop&crop=center&q=80', 28, 1),
('ASUS ZenBook Pro', 'ASUS ZenBook Pro 15 OLED i9-13900H RTX4060', 13999.00, 15, 7, 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400&h=400&fit=crop&crop=center&q=80', 21, 1),
('Microsoft Surface Pro', 'Microsoft Surface Pro 9 13-inch 2-in-1 Tablet i7', 8999.00, 35, 7, 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=400&h=400&fit=crop&crop=center&q=80', 38, 1),

-- Digital Accessories Category (6 items)
('AirPods Pro 2', 'Apple AirPods Pro 2nd Gen Active Noise Cancelling', 1899.00, 80, 8, 'https://images.unsplash.com/photo-1606400082777-ef05f3c5cde4?w=400&h=400&fit=crop&crop=center&q=80', 189, 1),
('Sony WH-1000XM5', 'Sony WH-1000XM5 Wireless Noise Cancelling Headphone', 2399.00, 60, 8, 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=400&h=400&fit=crop&crop=center&q=80', 145, 1),
('iPad Pro 12.9', 'Apple iPad Pro 12.9-inch M2 Chip 128GB WiFi', 8499.00, 40, 8, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop&crop=center&q=80', 67, 1),
('Apple Watch Ultra', 'Apple Watch Ultra 2 49mm Titanium Case Sports Band', 6399.00, 50, 8, 'https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400&h=400&fit=crop&crop=center&q=80', 89, 1),
('Anker Power Bank', 'Anker 20000mAh 65W Super Fast Charging Power Bank', 399.00, 200, 8, 'https://images.unsplash.com/photo-1609592853103-b27a0e0138bf?w=400&h=400&fit=crop&crop=center&q=80', 567, 1),
('Logitech MX Master 3S', 'Logitech MX Master 3S Wireless Mouse Office Essential', 699.00, 120, 8, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=400&fit=crop&crop=center&q=80', 234, 1),

-- Men Fashion Category (5 items)
('Nike Air Force 1', 'Nike Air Force 1 07 Classic White Sneakers Unisex', 899.00, 150, 9, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400&h=400&fit=crop&crop=center&q=80', 289, 1),
('Adidas Stan Smith', 'Adidas Stan Smith Green Tail White Classic Sneakers', 699.00, 180, 9, 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5a?w=400&h=400&fit=crop&crop=center&q=80', 234, 1),
('Uniqlo Down Jacket', 'Uniqlo Men Ultra Light Down Jacket Multi Colors', 599.00, 120, 9, 'https://images.unsplash.com/photo-1551928134-c4c5d4abaa2c?w=400&h=400&fit=crop&crop=center&q=80', 178, 1),
('Levis Jeans', 'Levis 511 Slim Fit Jeans Classic Blue Wash', 599.00, 100, 9, 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&h=400&fit=crop&crop=center&q=80', 156, 1),
('Champion Hoodie', 'Champion Classic Logo Hoodie Cotton Fleece', 399.00, 200, 9, 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=400&fit=crop&crop=center&q=80', 234, 1),

-- Women Fashion Category (5 items)
('ZARA Dress', 'ZARA Women Spring Summer Floral Dress French Vintage', 499.00, 100, 10, 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=400&h=400&fit=crop&crop=center&q=80', 167, 1),
('HM Blazer', 'HM Women Slim Fit Blazer Office Professional', 399.00, 80, 10, 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=400&h=400&fit=crop&crop=center&q=80', 98, 1),
('Vero Moda Sweater', 'Vero Moda Pure Wool V-neck Sweater Gentle Style', 399.00, 120, 10, 'https://images.unsplash.com/photo-1571513722275-4b8c9ee8ba5b?w=400&h=400&fit=crop&crop=center&q=80', 156, 1),
('COS Skirt', 'COS High Waist A-line Skirt Minimalist Design', 599.00, 90, 10, 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=400&h=400&fit=crop&crop=center&q=80', 123, 1),
('Uniqlo T-shirt', 'Uniqlo AIRism Cool Touch T-shirt Summer Essential', 99.00, 300, 10, 'https://images.unsplash.com/photo-1556821840-3a9fbc8e7696?w=400&h=400&fit=crop&crop=center&q=80', 456, 1),

-- Home Appliances Category (4 items) - Using category ID 12
('Dyson V15 Vacuum', 'Dyson V15 Detect Cordless Vacuum Laser Detection Tech', 4999.00, 40, 12, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400&h=400&fit=crop&crop=center&q=80', 89, 1),
('Xiaomi Robot Vacuum', 'Xiaomi Robot Vacuum S10+ Auto Dust Collection Laser Nav', 2999.00, 60, 12, 'https://images.unsplash.com/photo-1600298881974-6be191ceeda1?w=400&h=400&fit=crop&crop=center&q=80', 134, 1),
('Midea Washing Machine', 'Midea 10KG Inverter Drum Washing Machine Sterilizing', 2399.00, 30, 12, 'https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=400&h=400&fit=crop&crop=center&q=80', 67, 1),
('Gree Air Conditioner', 'Gree 1.5HP Inverter Air Conditioner Level 1 Energy', 3299.00, 25, 12, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400&h=400&fit=crop&crop=center&q=80', 78, 1);

-- Verify the number of inserted products
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

-- Display total product count
SELECT COUNT(*) as total_products FROM products WHERE status = 1;

COMMIT; 