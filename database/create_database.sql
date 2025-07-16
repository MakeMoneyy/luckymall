-- 招财商城数据库创建脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS lucky_mall 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 显示创建结果
SHOW DATABASES LIKE 'lucky_mall';

-- 使用数据库
USE lucky_mall;

SELECT 'Database lucky_mall created successfully!' as message; 