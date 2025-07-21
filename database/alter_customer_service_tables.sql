-- 设置字符集
SET NAMES utf8mb4;

-- 扩展CustomerServiceChat表
ALTER TABLE customer_service_chat 
ADD COLUMN recognized_intent VARCHAR(50) AFTER intent_type,
ADD COLUMN extracted_entities JSON AFTER recognized_intent,
ADD COLUMN emotion_type VARCHAR(20) AFTER extracted_entities,
ADD COLUMN emotion_intensity INT AFTER emotion_type,
ADD COLUMN transferred_to_human BOOLEAN DEFAULT FALSE AFTER emotion_intensity;

-- 创建人工客服会话表
CREATE TABLE human_service_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    ai_session_id VARCHAR(64),
    status ENUM('WAITING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'WAITING',
    staff_id VARCHAR(50),
    transfer_reason VARCHAR(255),
    emotion_data JSON,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_session (user_id, session_id),
    INDEX idx_status (status)
);

-- 创建人工客服消息表
CREATE TABLE human_service_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL,
    sender_type ENUM('USER', 'STAFF', 'SYSTEM') NOT NULL,
    sender_id VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session (session_id),
    INDEX idx_created_time (created_time)
);

-- 创建客服人员表
CREATE TABLE customer_service_staff (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    status ENUM('ONLINE', 'BUSY', 'OFFLINE') DEFAULT 'OFFLINE',
    max_concurrent_sessions INT DEFAULT 3,
    current_sessions INT DEFAULT 0,
    last_active_time DATETIME,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入测试客服人员数据
INSERT INTO customer_service_staff (id, name, status) VALUES
('staff001', '客服小王', 'ONLINE'),
('staff002', '客服小李', 'ONLINE'),
('staff003', '客服小张', 'OFFLINE'); 