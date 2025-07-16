-- 招财商城数据库初始化脚本
-- 使用数据库
USE lucky_mall;

-- 删除已存在的表（按依赖关系倒序删除）
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- 创建用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建商品分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    parent_id BIGINT COMMENT '父分类ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 创建商品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    category_id BIGINT COMMENT '分类ID',
    image_url VARCHAR(500) COMMENT '商品图片URL',
    sales_count INT DEFAULT 0 COMMENT '销量',
    status TINYINT DEFAULT 1 COMMENT '状态 1:上架 0:下架',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_category_id (category_id),
    INDEX idx_name (name),
    INDEX idx_price (price),
    INDEX idx_sales_count (sales_count),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 插入分类测试数据
INSERT INTO categories (name, description, parent_id) VALUES
('电子产品', '各类电子设备和数码产品', NULL),
('服装服饰', '男女服装和配饰', NULL),
('家居用品', '家庭生活用品', NULL),
('图书音像', '书籍、音乐和影视产品', NULL),
('运动户外', '运动器材和户外用品', NULL);

INSERT INTO categories (name, description, parent_id) VALUES
('手机通讯', '智能手机和通讯设备', 1),
('电脑办公', '笔记本电脑和办公设备', 1),
('数码配件', '充电器、数据线等配件', 1),
('男装', '男性服装', 2),
('女装', '女性服装', 2),
('箱包', '各类箱包', 2),
('厨房用品', '厨房电器和用具', 3),
('家纺用品', '床上用品等', 3);

-- 插入用户测试数据
INSERT INTO users (username, password, email, phone) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@luckymall.com', '13800138000'),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'test@luckymall.com', '13800138001'),
('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user001@luckymall.com', '13800138002');

-- 插入商品测试数据
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url, sales_count, status) VALUES
-- 手机通讯类商品
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB 深空黑色', 8999.00, 50, 6, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&h=400&fit=crop&crop=center', 128, 1),
('华为 Mate 60 Pro', '华为 Mate 60 Pro 512GB 雅黑色', 6999.00, 30, 6, 'https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=400&h=400&fit=crop&crop=center', 89, 1),
('小米 14', '小米 14 手机 12GB+256GB 黑色', 3999.00, 80, 6, 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=400&fit=crop&crop=center', 156, 1),
('OPPO Find X7', 'OPPO Find X7 智能手机 16GB+512GB', 4999.00, 45, 6, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop&crop=center', 67, 1),
('vivo X100 Pro', 'vivo X100 Pro 5G手机 16GB+512GB', 4999.00, 35, 6, 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=400&h=400&fit=crop&crop=center', 78, 1),

-- 电脑办公类商品
('MacBook Pro 16', 'Apple MacBook Pro 16英寸 M3 Pro芯片', 19999.00, 20, 7, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop&crop=center', 45, 1),
('联想ThinkPad X1', '联想ThinkPad X1 Carbon 14英寸商务笔记本', 12999.00, 25, 7, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop&crop=center', 32, 1),
('戴尔XPS 13', '戴尔XPS 13英寸超轻薄笔记本电脑', 8999.00, 30, 7, 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop&crop=center', 28, 1),
('华硕ZenBook', '华硕ZenBook 14英寸轻薄笔记本', 6999.00, 40, 7, 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400&h=400&fit=crop&crop=center', 41, 1),
('惠普战66', '惠普战66五代 14英寸商务笔记本', 4999.00, 55, 7, 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=400&h=400&fit=crop&crop=center', 38, 1),

-- 数码配件类商品
('苹果无线充电器', 'Apple MagSafe 无线充电器', 329.00, 100, 8, 'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=400&h=400&fit=crop&crop=center', 234, 1),
('小米充电宝', '小米移动电源3 20000mAh 快充版', 149.00, 200, 8, 'https://images.unsplash.com/photo-1609592853103-b27a0e0138bf?w=400&h=400&fit=crop&crop=center', 567, 1),
('AirPods Pro 2', 'Apple AirPods Pro 第二代', 1899.00, 80, 8, 'https://images.unsplash.com/photo-1606400082777-ef05f3c5cde4?w=400&h=400&fit=crop&crop=center', 189, 1),
('华为FreeBuds', '华为FreeBuds Pro 3 无线耳机', 1499.00, 90, 8, 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=400&h=400&fit=crop&crop=center', 145, 1),
('Anker数据线', 'Anker USB-C to Lightning 数据线 2米', 89.00, 300, 8, 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400&h=400&fit=crop&crop=center', 456, 1),

-- 男装类商品
('优衣库羽绒服', '优衣库男装轻型羽绒服 黑色', 499.00, 120, 9, 'https://images.unsplash.com/photo-1551928134-c4c5d4abaa2c?w=400&h=400&fit=crop&crop=center', 234, 1),
('Nike运动套装', 'Nike男士运动套装 春季新款', 899.00, 80, 9, 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=400&fit=crop&crop=center', 156, 1),
('Adidas运动鞋', 'Adidas男士运动鞋 黑白配色', 699.00, 150, 9, 'https://images.unsplash.com/photo-1544966503-7cc5ac882d5a?w=400&h=400&fit=crop&crop=center', 289, 1),
('海澜之家衬衫', '海澜之家男士商务长袖衬衫', 299.00, 200, 9, 'https://images.unsplash.com/photo-1603252109303-2751441b4c82?w=400&h=400&fit=crop&crop=center', 178, 1),
('李宁运动裤', '李宁男士运动长裤 宽松版型', 299.00, 180, 9, 'https://images.unsplash.com/photo-1506629905607-47b252040a27?w=400&h=400&fit=crop&crop=center', 134, 1),

-- 女装类商品
('ZARA连衣裙', 'ZARA女装春季新款连衣裙', 399.00, 100, 10, 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=400&h=400&fit=crop&crop=center', 167, 1),
('H&M针织衫', 'H&M女装基础款针织衫 多色可选', 199.00, 150, 10, 'https://images.unsplash.com/photo-1571513722275-4b8c9ee8ba5b?w=400&h=400&fit=crop&crop=center', 245, 1),
('Only外套', 'Only女装春季薄款外套', 599.00, 80, 10, 'https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=400&h=400&fit=crop&crop=center', 98, 1),
('Vero Moda裙子', 'Vero Moda半身裙 A字型设计', 299.00, 120, 10, 'https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=400&h=400&fit=crop&crop=center', 156, 1),
('UR卫衣', 'UR女装连帽卫衣 宽松版型', 399.00, 90, 10, 'https://images.unsplash.com/photo-1556821840-3a9fbc8e7696?w=400&h=400&fit=crop&crop=center', 123, 1),

-- 箱包类商品
('新秀丽拉杆箱', '新秀丽20寸登机箱 万向轮', 899.00, 60, 11, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop&crop=center', 89, 1),
('小米背包', '小米都市背包 简约商务款', 199.00, 200, 11, 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=400&fit=crop&crop=center', 234, 1),
('Coach手袋', 'Coach女士手提包 经典款', 2999.00, 30, 11, 'https://images.unsplash.com/photo-1594223274512-ad4803739b7c?w=400&h=400&fit=crop&crop=center', 45, 1),
('Nike双肩包', 'Nike运动双肩包 大容量', 299.00, 150, 11, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=400&fit=crop&crop=center', 178, 1),
('Kipling斜挎包', 'Kipling女士斜挎包 轻便款', 599.00, 80, 11, 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400&h=400&fit=crop&crop=center', 67, 1),

-- 厨房用品类商品
('九阳豆浆机', '九阳免滤豆浆机 多功能款', 399.00, 100, 12, 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400&h=400&fit=crop&crop=center', 234, 1),
('美的电饭煲', '美的IH电饭煲 4L容量 智能预约', 599.00, 80, 12, 'https://images.unsplash.com/photo-1585515656968-b355f5c52c7c?w=400&h=400&fit=crop&crop=center', 189, 1),
('苏泊尔炒锅', '苏泊尔不锈钢炒锅 32cm无油烟', 299.00, 120, 12, 'https://images.unsplash.com/photo-1556909114-c4bb37e7c8b7?w=400&h=400&fit=crop&crop=center', 156, 1),
('小熊养生壶', '小熊多功能养生壶 玻璃材质', 199.00, 150, 12, 'https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?w=400&h=400&fit=crop&crop=center', 267, 1),
('格兰仕微波炉', '格兰仕微波炉 23L机械式', 399.00, 90, 12, 'https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=400&h=400&fit=crop&crop=center', 123, 1),

-- 家纺用品类商品
('水星家纺四件套', '水星家纺纯棉四件套 1.8m床用', 299.00, 100, 13, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=400&h=400&fit=crop&crop=center', 178, 1),
('富安娜被子', '富安娜羽丝绒被 冬被加厚保暖', 599.00, 80, 13, 'https://images.unsplash.com/photo-1591088398332-8a7791972843?w=400&h=400&fit=crop&crop=center', 134, 1),
('罗莱枕头', '罗莱记忆棉枕头 护颈椎', 199.00, 150, 13, 'https://images.unsplash.com/photo-1584464491033-06628f3a6b7b?w=400&h=400&fit=crop&crop=center', 234, 1),
('梦洁毛毯', '梦洁法兰绒毛毯 双人加厚', 199.00, 120, 13, 'https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=400&h=400&fit=crop&crop=center', 189, 1),
('博洋地毯', '博洋客厅地毯 简约现代风格', 399.00, 60, 13, 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400&h=400&fit=crop&crop=center', 78, 1);

-- 创建索引优化查询性能
CREATE INDEX idx_products_name_price ON products(name, price);
CREATE INDEX idx_products_category_price ON products(category_id, price);
CREATE INDEX idx_products_sales_price ON products(sales_count DESC, price);

-- 查询验证数据
SELECT 
    c.name as category_name,
    COUNT(p.id) as product_count,
    AVG(p.price) as avg_price,
    SUM(p.sales_count) as total_sales
FROM categories c
LEFT JOIN products p ON c.id = p.category_id
WHERE c.parent_id IS NOT NULL
GROUP BY c.id, c.name
ORDER BY product_count DESC;

COMMIT; 