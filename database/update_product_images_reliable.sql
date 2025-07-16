-- 更新商品图片URL - 使用可靠的Unsplash图片源
USE lucky_mall;

-- 📱 手机类（5款） - 使用高质量手机产品图片
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'iPhone 15 Pro';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Huawei Mate 60 Pro';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Xiaomi 14 Ultra';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'OPPO Find X7 Pro';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'vivo X100 Pro';

-- 💻 电脑类（5款） - 使用高质量笔记本电脑图片
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'MacBook Pro 16';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Lenovo ThinkPad X1';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Dell XPS 13';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'ASUS ZenBook Pro';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Microsoft Surface Pro';

-- 🎧 配件类（6款） - 使用电子产品配件图片
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1606400082777-ef05f3c5cde4?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'AirPods Pro 2';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Sony WH-1000XM5';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1561154464-82e9adf32764?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'iPad Pro 12.9';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1579586337278-3f436f25d4d6?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Apple Watch Ultra';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Anker Power Bank';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Logitech MX Master 3S';

-- 👕 男装类（5款） - 使用服装产品图片
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5a?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Nike Air Force 1';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Adidas Stan Smith';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1551928134-c4c5d4abaa2c?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Uniqlo Down Jacket';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Levis Jeans';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Champion Hoodie';

-- 👗 女装类（5款） - 使用女装产品图片
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'ZARA Dress';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'HM Blazer';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1571513722275-4b8c9ee8ba5b?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Vero Moda Sweater';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'COS Skirt';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Uniqlo T-shirt';

-- 🏠 家电类（4款） - 使用家电产品图片
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Dyson V15 Vacuum';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1600298881974-6be191ceeda1?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Xiaomi Robot Vacuum';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Midea Washing Machine';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500&h=500&fit=crop&crop=center&q=80' WHERE name = 'Gree Air Conditioner';

-- 显示更新结果
SELECT 'Product images updated with reliable URLs!' as result;

-- 验证更新结果
SELECT id, name, LEFT(image_url, 70) as updated_image 
FROM products 
WHERE status = 1 
ORDER BY id; 