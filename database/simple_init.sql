USE lucky_mall;

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    level TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category_id BIGINT,
    image_url VARCHAR(500),
    sales_count INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入分类数据
INSERT INTO categories (id, name, parent_id, level, sort_order, status) VALUES
(6, 'Mobile Phone', NULL, 1, 6, 1),
(7, 'Computer', NULL, 1, 7, 1),
(8, 'Digital Accessories', NULL, 1, 8, 1),
(9, 'Men Fashion', NULL, 1, 9, 1),
(10, 'Women Fashion', NULL, 1, 10, 1);

-- 插入商品数据
INSERT INTO products (name, description, price, stock_quantity, category_id, image_url, sales_count, status) VALUES
('iPhone 15 Pro', 'Apple iPhone 15 Pro 256GB Space Black', 8999.00, 50, 6, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&h=400&fit=crop&crop=center', 128, 1),
('Huawei Mate60 Pro', 'Huawei Mate 60 Pro 512GB Black', 6999.00, 30, 6, 'https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=400&h=400&fit=crop&crop=center', 89, 1),
('MacBook Pro 16', 'Apple MacBook Pro 16 inch M3 Pro', 19999.00, 20, 7, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop&crop=center', 45, 1),
('ThinkPad X1', 'Lenovo ThinkPad X1 Carbon 14 inch', 12999.00, 25, 7, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop&crop=center', 32, 1),
('AirPods Pro 2', 'Apple AirPods Pro 2nd Generation', 1899.00, 80, 8, 'https://images.unsplash.com/photo-1606400082777-ef05f3c5cde4?w=400&h=400&fit=crop&crop=center', 189, 1);

-- 创建索引
CREATE INDEX idx_products_name_price ON products(name, price);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_categories_parent_id ON categories(parent_id); 