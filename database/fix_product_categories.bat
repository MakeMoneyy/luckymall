@echo off
echo ========================================
echo 招财商城 - 商品分类错误修复工具
echo ========================================
echo.
echo 准备修复以下问题：
echo 1. 服装鞋帽类是空的
echo 2. 服装鞋帽和洗衣机空调都在其他数码产品分类  
echo 3. 有些手机在家居生活分类
echo.
echo 修复后将有5个清晰的分类：
echo 1. 手机
echo 2. 电脑
echo 3. 其他数码产品
echo 4. 服装
echo 5. 家居生活
echo.

pause

echo 开始执行分类修复...

echo [1/10] 查看当前分类分布...
mysql -u root -p123456 lucky_mall < database/fix_product_categories.sql

echo.
echo ========================================
echo 商品分类修复完成！
echo ========================================
echo.
echo 现在您可以：
echo 1. 刷新前端页面查看效果
echo 2. 检查各分类下的商品是否正确
echo 3. 验证主页分类区域显示
echo.
pause 