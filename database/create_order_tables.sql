-- 创建订单相关表
USE lucky_mall;

-- 创建用户地址表
CREATE TABLE user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    postal_code VARCHAR(10) COMMENT '邮政编码',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址 1:是 0:否',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 创建分期方案表
CREATE TABLE installment_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分期方案ID',
    plan_name VARCHAR(50) NOT NULL COMMENT '方案名称',
    installment_count INT NOT NULL COMMENT '分期期数',
    interest_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '利率（%）',
    min_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '最小分期金额',
    max_amount DECIMAL(10,2) DEFAULT 999999.99 COMMENT '最大分期金额',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分期方案表';

-- 创建订单主表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_status VARCHAR(20) DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态',
    payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态',
    payment_method VARCHAR(20) COMMENT '支付方式',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    
    -- 收货地址信息（订单创建时复制，避免地址变更影响历史订单）
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(500) NOT NULL COMMENT '收货地址',
    
    -- 分期付款信息
    is_installment TINYINT DEFAULT 0 COMMENT '是否分期付款 1:是 0:否',
    installment_plan_id BIGINT COMMENT '分期方案ID',
    installment_count INT COMMENT '分期期数',
    monthly_amount DECIMAL(10,2) COMMENT '每期金额',
    
    order_remark VARCHAR(500) COMMENT '订单备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    paid_at TIMESTAMP NULL COMMENT '支付时间',
    shipped_at TIMESTAMP NULL COMMENT '发货时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (installment_plan_id) REFERENCES installment_plans(id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_order_status (order_status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- 创建订单商品表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单商品ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(255) NOT NULL COMMENT '商品名称（快照）',
    product_image VARCHAR(500) COMMENT '商品图片（快照）',
    product_price DECIMAL(10,2) NOT NULL COMMENT '商品价格（快照）',
    quantity INT NOT NULL COMMENT '购买数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';

-- 创建支付记录表
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID',
    payment_no VARCHAR(32) UNIQUE NOT NULL COMMENT '支付流水号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    payment_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '支付状态',
    
    -- 分期付款相关
    is_installment TINYINT DEFAULT 0 COMMENT '是否分期支付',
    installment_order INT COMMENT '第几期',
    installment_total INT COMMENT '总期数',
    
    -- 第三方支付信息
    third_party_no VARCHAR(64) COMMENT '第三方支付流水号',
    third_party_response TEXT COMMENT '第三方支付响应',
    
    payment_time TIMESTAMP NULL COMMENT '支付时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_no (payment_no),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

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

-- 验证表创建
SHOW CREATE TABLE orders;
SHOW CREATE TABLE order_items;
SHOW CREATE TABLE user_addresses;
SHOW CREATE TABLE installment_plans;
SHOW CREATE TABLE payments;

SELECT 'Order tables created successfully!' as message; 