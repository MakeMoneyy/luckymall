USE lucky_mall;

-- 修复剩余有问题的商品图片
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?w=500&h=500&fit=crop&crop=center&q=80' WHERE id = 24;
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500&h=500&fit=crop&crop=center&q=80' WHERE id = 27;

SELECT 'Remaining images fixed!' as result; 