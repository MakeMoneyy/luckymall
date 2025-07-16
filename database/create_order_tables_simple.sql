USE lucky_mall;

-- 删除已存在的表
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS user_addresses;
DROP TABLE IF EXISTS installment_plans;

-- 创建分期方案表
CREATE TABLE installment_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL,
    installment_count INT NOT NULL,
    interest_rate DECIMAL(5,2) DEFAULT 0.00,
    min_amount DECIMAL(10,2) DEFAULT 0.00,
    max_amount DECIMAL(10,2) DEFAULT 999999.99,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建用户地址表
CREATE TABLE user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10),
    is_default TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建订单主表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    order_status VARCHAR(20) DEFAULT 'PENDING_PAYMENT',
    payment_status VARCHAR(20) DEFAULT 'UNPAID',
    payment_method VARCHAR(20),
    total_amount DECIMAL(10,2) NOT NULL,
    actual_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    shipping_fee DECIMAL(10,2) DEFAULT 0.00,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(500) NOT NULL,
    is_installment TINYINT DEFAULT 0,
    installment_plan_id BIGINT,
    installment_count INT,
    monthly_amount DECIMAL(10,2),
    order_remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    shipped_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (installment_plan_id) REFERENCES installment_plans(id)
);

-- 创建订单商品表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_image VARCHAR(500),
    product_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 创建支付记录表
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_no VARCHAR(32) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    is_installment TINYINT DEFAULT 0,
    installment_order INT,
    installment_total INT,
    third_party_no VARCHAR(64),
    third_party_response TEXT,
    payment_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 插入分期方案数据
INSERT INTO installment_plans (plan_name, installment_count, interest_rate, min_amount, max_amount, status) VALUES
('3期免息', 3, 0.00, 500.00, 50000.00, 1),
('6期免息', 6, 0.00, 1000.00, 30000.00, 1),
('12期免息', 12, 0.00, 2000.00, 20000.00, 1),
('24期低息', 24, 0.50, 5000.00, 50000.00, 1);

-- 插入测试地址数据
INSERT INTO user_addresses (user_id, receiver_name, receiver_phone, province, city, district, detail_address, postal_code, is_default) VALUES
(1, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国门外大街1号国贸大厦A座1001室', '100020', 1),
(1, '李四', '13800138002', '上海市', '上海市', '浦东新区', '陆家嘴金融中心2号楼2001室', '200120', 0),
(2, '王五', '13800138003', '广东省', '深圳市', '南山区', '科技园南区深圳湾1号T3大厦3001室', '518000', 1);

SELECT 'Order tables created successfully!' as message; 