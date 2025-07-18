-- 设置字符集
SET NAMES utf8mb4;

-- 客服对话记录表
CREATE TABLE customer_service_chat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    session_id VARCHAR(64),
    user_message TEXT,
    bot_response TEXT,
    intent_type VARCHAR(32),
    cache_hit BOOLEAN DEFAULT FALSE,
    response_time_ms INT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_session (user_id, session_id),
    INDEX idx_created_time (created_time)
);

-- 用户信用卡信息表
CREATE TABLE user_credit_card (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    card_level ENUM('GOLD', 'PLATINUM', 'DIAMOND') DEFAULT 'GOLD',
    points_balance INT DEFAULT 0,
    points_expiring INT DEFAULT 0,
    expiring_date DATE,
    credit_limit DECIMAL(10,2),
    bill_date INT,
    due_date INT,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id)
);

-- FAQ知识库表
CREATE TABLE faq_knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question VARCHAR(255) NOT NULL,
    answer TEXT NOT NULL,
    category VARCHAR(32),
    credit_card_promotion TEXT,
    hit_count INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_status (status)
);

-- 用户行为分析表
CREATE TABLE user_behavior_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    session_id VARCHAR(64),
    action_type VARCHAR(32),
    action_data JSON,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_time (user_id, created_time),
    INDEX idx_action_type (action_type)
);

-- 插入测试用户信用卡数据
INSERT INTO user_credit_card (user_id, card_level, points_balance, points_expiring, expiring_date, credit_limit, bill_date, due_date) VALUES
(1, 'PLATINUM', 8500, 2000, '2024-04-15', 50000.00, 10, 25),
(2, 'GOLD', 3200, 800, '2024-03-30', 20000.00, 15, 30),
(3, 'DIAMOND', 15000, 5000, '2024-05-10', 100000.00, 5, 20);

-- 插入基础FAQ数据
INSERT INTO faq_knowledge (question, answer, category, credit_card_promotion) VALUES
('订单什么时候发货？', '您的订单预计明天发货哦～', 'shipping', '💡小贴士：下次购买建议使用信用卡支付，可以享受物流保险，万一包裹丢失或损坏，招行为您全额赔付！'),
('可以退货吗？', '当然可以退货！7天无理由退换。', 'return', '🎯特别说明：信用卡支付的订单，退货更便捷，还享受退货运费险，完全零风险购物体验！'),
('这个商品可以分期吗？', '当然可以分期！', 'payment', '使用您的招商银行信用卡，享受12期免息分期，还能获得积分奖励和购物保险保障！'),
('积分有什么用？', '您的招行积分可是很值钱的哦！', 'points', '💎 可直接抵扣现金使用 🎁 兑换精美礼品 ⚡ 建议您立即使用积分抵扣部分费用，再用信用卡支付剩余金额获得新积分！'),
('有什么优惠吗？', '当然有优惠！', 'discount', '🎁 使用信用卡支付立享8.8折优惠 💰 额外获得双倍积分 ⏰ 享受48天超长免息期'); 