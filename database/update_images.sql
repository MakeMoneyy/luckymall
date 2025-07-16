-- 更新商品图片URL脚本
USE lucky_mall;

-- 更新手机通讯类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&h=400&fit=crop&crop=center' WHERE name = 'iPhone 15 Pro';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=400&h=400&fit=crop&crop=center' WHERE name = '华为 Mate 60 Pro';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=400&fit=crop&crop=center' WHERE name = '小米 14';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop&crop=center' WHERE name = 'OPPO Find X7';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=400&h=400&fit=crop&crop=center' WHERE name = 'vivo X100 Pro';

-- 更新电脑办公类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop&crop=center' WHERE name = 'MacBook Pro 16';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop&crop=center' WHERE name = '联想ThinkPad X1';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop&crop=center' WHERE name = '戴尔XPS 13';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400&h=400&fit=crop&crop=center' WHERE name = '华硕ZenBook';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=400&h=400&fit=crop&crop=center' WHERE name = '惠普战66';

-- 更新数码配件类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=400&h=400&fit=crop&crop=center' WHERE name = '苹果无线充电器';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1609592853103-b27a0e0138bf?w=400&h=400&fit=crop&crop=center' WHERE name = '小米充电宝';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1606400082777-ef05f3c5cde4?w=400&h=400&fit=crop&crop=center' WHERE name = 'AirPods Pro 2';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=400&h=400&fit=crop&crop=center' WHERE name = '华为FreeBuds';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400&h=400&fit=crop&crop=center' WHERE name = 'Anker数据线';

-- 更新男装类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1551928134-c4c5d4abaa2c?w=400&h=400&fit=crop&crop=center' WHERE name = '优衣库羽绒服';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=400&fit=crop&crop=center' WHERE name = 'Nike运动套装';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5a?w=400&h=400&fit=crop&crop=center' WHERE name = 'Adidas运动鞋';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1603252109303-2751441b4c82?w=400&h=400&fit=crop&crop=center' WHERE name = '海澜之家衬衫';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1506629905607-47b252040a27?w=400&h=400&fit=crop&crop=center' WHERE name = '李宁运动裤';

-- 更新女装类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=400&h=400&fit=crop&crop=center' WHERE name = 'ZARA连衣裙';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1571513722275-4b8c9ee8ba5b?w=400&h=400&fit=crop&crop=center' WHERE name = 'H&M针织衫';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=400&h=400&fit=crop&crop=center' WHERE name = 'Only外套';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=400&h=400&fit=crop&crop=center' WHERE name = 'Vero Moda裙子';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1556821840-3a9fbc8e7696?w=400&h=400&fit=crop&crop=center' WHERE name = 'UR卫衣';

-- 更新箱包类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop&crop=center' WHERE name = '新秀丽拉杆箱';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=400&fit=crop&crop=center' WHERE name = '小米背包';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1594223274512-ad4803739b7c?w=400&h=400&fit=crop&crop=center' WHERE name = 'Coach手袋';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop&crop=center' WHERE name = 'Nike双肩包';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400&h=400&fit=crop&crop=center' WHERE name = 'Kipling斜挎包';

-- 更新厨房用品类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400&h=400&fit=crop&crop=center' WHERE name = '九阳豆浆机';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1585515656968-b355f5c52c7c?w=400&h=400&fit=crop&crop=center' WHERE name = '美的电饭煲';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1556909114-c4bb37e7c8b7?w=400&h=400&fit=crop&crop=center' WHERE name = '苏泊尔炒锅';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?w=400&h=400&fit=crop&crop=center' WHERE name = '小熊养生壶';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=400&h=400&fit=crop&crop=center' WHERE name = '格兰仕微波炉';

-- 更新家纺用品类商品
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400&h=400&fit=crop&crop=center' WHERE name = '水星家纺四件套';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1591088398332-8a7791972843?w=400&h=400&fit=crop&crop=center' WHERE name = '富安娜被子';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b?w=400&h=400&fit=crop&crop=center' WHERE name = '罗莱枕头';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=400&h=400&fit=crop&crop=center' WHERE name = '梦洁毛毯';
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400&h=400&fit=crop&crop=center' WHERE name = '博洋地毯';

SELECT 'Image URLs updated successfully' as result; 