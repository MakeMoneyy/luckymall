-- 优化客服系统数据库表结构和索引

-- 1. 为customer_service_chat表添加索引
ALTER TABLE customer_service_chat ADD INDEX idx_user_session (user_id, session_id);
ALTER TABLE customer_service_chat ADD INDEX idx_session (session_id);
ALTER TABLE customer_service_chat ADD INDEX idx_created_time (created_time);
ALTER TABLE customer_service_chat ADD INDEX idx_intent_type (intent_type);

-- 2. 为human_service_session表添加索引
ALTER TABLE human_service_session ADD INDEX idx_user_session (user_id, session_id);
ALTER TABLE human_service_session ADD INDEX idx_session (session_id);
ALTER TABLE human_service_session ADD INDEX idx_status (status);
ALTER TABLE human_service_session ADD INDEX idx_staff (staff_id);
ALTER TABLE human_service_session ADD INDEX idx_created_time (created_time);

-- 3. 为faq_knowledge表添加全文索引
ALTER TABLE faq_knowledge ADD FULLTEXT INDEX ft_idx_question_answer (question, answer);
ALTER TABLE faq_knowledge ADD INDEX idx_category (category);

-- 4. 优化customer_service_chat表结构，添加情感分析和意图识别字段
ALTER TABLE customer_service_chat ADD COLUMN emotion_type VARCHAR(20) COMMENT '情绪类型: POSITIVE, NEUTRAL, NEGATIVE' AFTER response_time;
ALTER TABLE customer_service_chat ADD COLUMN emotion_intensity INT COMMENT '情绪强度: 1-5' AFTER emotion_type;
ALTER TABLE customer_service_chat ADD COLUMN intent_type VARCHAR(50) COMMENT '意图类型' AFTER emotion_intensity;
ALTER TABLE customer_service_chat ADD COLUMN intent_confidence FLOAT COMMENT '意图置信度' AFTER intent_type;
ALTER TABLE customer_service_chat ADD COLUMN extracted_entities TEXT COMMENT '提取的实体信息(JSON格式)' AFTER intent_confidence;

-- 5. 创建查询优化视图
CREATE OR REPLACE VIEW v_recent_customer_chats AS
SELECT 
    c.id,
    c.user_id,
    c.session_id,
    c.message,
    c.response,
    c.emotion_type,
    c.emotion_intensity,
    c.intent_type,
    c.intent_confidence,
    c.created_time
FROM 
    customer_service_chat c
WHERE 
    c.created_time > DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY 
    c.created_time DESC;

-- 6. 创建情感分析统计视图
CREATE OR REPLACE VIEW v_emotion_stats AS
SELECT 
    DATE(created_time) as chat_date,
    emotion_type,
    COUNT(*) as count,
    AVG(emotion_intensity) as avg_intensity
FROM 
    customer_service_chat
WHERE 
    emotion_type IS NOT NULL
GROUP BY 
    DATE(created_time), emotion_type;

-- 7. 创建意图识别统计视图
CREATE OR REPLACE VIEW v_intent_stats AS
SELECT 
    DATE(created_time) as chat_date,
    intent_type,
    COUNT(*) as count,
    AVG(intent_confidence) as avg_confidence
FROM 
    customer_service_chat
WHERE 
    intent_type IS NOT NULL
GROUP BY 
    DATE(created_time), intent_type;

-- 8. 创建人工客服转接统计视图
CREATE OR REPLACE VIEW v_human_service_stats AS
SELECT 
    DATE(created_time) as transfer_date,
    status,
    COUNT(*) as count,
    AVG(TIMESTAMPDIFF(SECOND, created_time, updated_time)) as avg_response_time_seconds
FROM 
    human_service_session
GROUP BY 
    DATE(created_time), status;

-- 9. 添加分区（可选，取决于数据量）
-- 如果数据量很大，可以考虑按月分区
-- ALTER TABLE customer_service_chat PARTITION BY RANGE (TO_DAYS(created_time)) (
--     PARTITION p202301 VALUES LESS THAN (TO_DAYS('2023-02-01')),
--     PARTITION p202302 VALUES LESS THAN (TO_DAYS('2023-03-01')),
--     PARTITION p202303 VALUES LESS THAN (TO_DAYS('2023-04-01')),
--     PARTITION p202304 VALUES LESS THAN (TO_DAYS('2023-05-01')),
--     PARTITION p202305 VALUES LESS THAN (TO_DAYS('2023-06-01')),
--     PARTITION p202306 VALUES LESS THAN (TO_DAYS('2023-07-01')),
--     PARTITION p202307 VALUES LESS THAN (TO_DAYS('2023-08-01')),
--     PARTITION p202308 VALUES LESS THAN (TO_DAYS('2023-09-01')),
--     PARTITION p202309 VALUES LESS THAN (TO_DAYS('2023-10-01')),
--     PARTITION p202310 VALUES LESS THAN (TO_DAYS('2023-11-01')),
--     PARTITION p202311 VALUES LESS THAN (TO_DAYS('2023-12-01')),
--     PARTITION p202312 VALUES LESS THAN (TO_DAYS('2024-01-01')),
--     PARTITION pmax VALUES LESS THAN MAXVALUE
-- );

-- 10. 优化查询存储过程
DELIMITER //

-- 获取用户最近的聊天记录
CREATE PROCEDURE sp_get_recent_user_chats(
    IN p_user_id BIGINT,
    IN p_limit INT
)
BEGIN
    SELECT 
        c.id,
        c.session_id,
        c.message,
        c.response,
        c.emotion_type,
        c.intent_type,
        c.created_time
    FROM 
        customer_service_chat c
    WHERE 
        c.user_id = p_user_id
    ORDER BY 
        c.created_time DESC
    LIMIT p_limit;
END //

-- 获取会话的聊天历史
CREATE PROCEDURE sp_get_session_history(
    IN p_session_id VARCHAR(64)
)
BEGIN
    SELECT 
        c.id,
        c.user_id,
        c.message,
        c.response,
        c.emotion_type,
        c.emotion_intensity,
        c.intent_type,
        c.created_time
    FROM 
        customer_service_chat c
    WHERE 
        c.session_id = p_session_id
    ORDER BY 
        c.created_time ASC;
END //

-- 获取情感分析统计
CREATE PROCEDURE sp_get_emotion_stats(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        chat_date,
        emotion_type,
        count,
        avg_intensity
    FROM 
        v_emotion_stats
    WHERE 
        chat_date BETWEEN p_start_date AND p_end_date
    ORDER BY 
        chat_date, emotion_type;
END //

-- 获取意图识别统计
CREATE PROCEDURE sp_get_intent_stats(
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        chat_date,
        intent_type,
        count,
        avg_confidence
    FROM 
        v_intent_stats
    WHERE 
        chat_date BETWEEN p_start_date AND p_end_date
    ORDER BY 
        chat_date, intent_type;
END //

DELIMITER ;

-- 11. 添加触发器，自动更新统计表（可选）
-- 如果需要实时统计，可以添加触发器

-- 12. 创建定期清理旧数据的事件（可选）
DELIMITER //

CREATE EVENT IF NOT EXISTS e_clean_old_chat_data
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- 删除超过6个月的聊天记录
    DELETE FROM customer_service_chat 
    WHERE created_time < DATE_SUB(NOW(), INTERVAL 6 MONTH);
    
    -- 删除超过3个月的人工客服会话
    DELETE FROM human_service_session 
    WHERE created_time < DATE_SUB(NOW(), INTERVAL 3 MONTH);
END //

DELIMITER ; 