-- 更新商品图片URL - 使用官方产品图片
USE lucky_mall;

-- 📱 手机类（5款）
UPDATE products SET image_url = 'https://www.apple.com/newsroom/images/product/iphone/standard/Apple_iPhone-15-Pro_hero.jpg' WHERE name = 'iPhone 15 Pro';
UPDATE products SET image_url = 'https://consumer.huawei.com/content/dam/huawei-cbg-site/common/mkt/pdp/phones/mate60-pro/img/mate60-pro-kv-white.png' WHERE name = 'Huawei Mate 60 Pro';
UPDATE products SET image_url = 'https://i01.appmifile.com/webfile/globalimg/products/pc/xiaomilux14ultra/m740-render/origin.png' WHERE name = 'Xiaomi 14 Ultra';
UPDATE products SET image_url = 'https://image.oppo.com/content/dam/oppo/common/mkt/v2-2/find-x7-pro/main/pc/find-x7-pro-white.png' WHERE name = 'OPPO Find X7 Pro';
UPDATE products SET image_url = 'https://www.vivo.com/static/images/products/x100-pro/blue.png' WHERE name = 'vivo X100 Pro';

-- 💻 电脑类（5款）
UPDATE products SET image_url = 'https://www.apple.com/v/macbook-pro-16/b/images/overview/hero_endframe__e6khcva4hkeq_large.jpg' WHERE name = 'MacBook Pro 16';
UPDATE products SET image_url = 'https://www.lenovo.com/medias/lenovo-laptop-thinkpad-x1-carbon-gen-11-subseries-hero.png' WHERE name = 'Lenovo ThinkPad X1';
UPDATE products SET image_url = 'https://i.dell.com/sites/csimages/Master_Imagery/all/xps-13-9320-laptop.jpg' WHERE name = 'Dell XPS 13';
UPDATE products SET image_url = 'https://dlcdnwebimgs.asus.com/gain/218f0b88-7804-4e16-ab8a-fbd653dff500/' WHERE name = 'ASUS ZenBook Pro';
UPDATE products SET image_url = 'https://c.s-microsoft.com/en-us/CMSImages/Surface_Pro_9_Platinum_Laptop_Shot.png' WHERE name = 'Microsoft Surface Pro';

-- 🎧 配件类（6款）
UPDATE products SET image_url = 'https://www.apple.com/v/airpods-pro-2nd-generation/a/images/overview/hero__e2sshtfqz0yu_large.jpg' WHERE name = 'AirPods Pro 2';
UPDATE products SET image_url = 'https://www.sony.com/image/sony-wh-1000xm5-black-product.jpg' WHERE name = 'Sony WH-1000XM5';
UPDATE products SET image_url = 'https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/ipad-pro-12-9-select-202209?wid=2000&hei=2000&fmt=jpeg&qlt=80' WHERE name = 'iPad Pro 12.9';
UPDATE products SET image_url = 'https://www.apple.com/v/watch/ultra/b/images/overview/hero/ultra_hero__f1blc3s5lqse_large.jpg' WHERE name = 'Apple Watch Ultra';
UPDATE products SET image_url = 'https://d2211byn0pk9fi.cloudfront.net/spree/images/attachments/000/003/501/original/anker-65w-powerbank.png' WHERE name = 'Anker Power Bank';
UPDATE products SET image_url = 'https://resource.logitech.com/w_1200,h_630,c_limit,q_auto,f_auto,dpr_1/d_transparent.gif/content/dam/logitech/en/products/mice/mx-master-3s/mx-master-3s-top-view.png' WHERE name = 'Logitech MX Master 3S';

-- 👕 男装类（5款）
UPDATE products SET image_url = 'https://static.nike.com/a/images/t_PDP_1280_v1/f_auto,q_auto:eco/air-force-1-07-shoe-5QFpJ5.png' WHERE name = 'Nike Air Force 1';
UPDATE products SET image_url = 'https://assets.adidas.com/images/h_840,f_auto,q_auto:sensitive,fl_lossy/Stan_Smith_Shoes_White_GZ5478_01_standard.jpg' WHERE name = 'Adidas Stan Smith';
UPDATE products SET image_url = 'https://image.uniqlo.com/UQ/ST3/WesternCommon/imagesgoods/444075/item/goods_69_444075.jpg' WHERE name = 'Uniqlo Down Jacket';
UPDATE products SET image_url = 'https://www.levi.com/levi/medias/sys_master/9291898172510/512692069-FARO-L.jpg' WHERE name = 'Levis Jeans';
UPDATE products SET image_url = 'https://www.champion.com/dw/image/v2/BBCL_PRD/on/demandware.static/-/Sites-master-product-catalog/default/dw9aa2933e/prodpdp/223152W_630.jpg' WHERE name = 'Champion Hoodie';

-- 👗 女装类（5款）
UPDATE products SET image_url = 'https://static.zara.net/photos///mkt/sp2023/collection/3918/110/711/2/w/560/3918110711_2_1_1.jpg' WHERE name = 'ZARA Dress';
UPDATE products SET image_url = 'https://www2.hm.com/content/dam/hmgoepprod/03/dc0/03dc0ee1a7976b191e044b2dbdfe4e0c9f2e49b0.jpg' WHERE name = 'HM Blazer';
UPDATE products SET image_url = 'https://static.veromoda.com/501/elite-collection-vero-moda-elite-pure-wool-sweater-l-m-collar-560417-1461208-1.jpg' WHERE name = 'Vero Moda Sweater';
UPDATE products SET image_url = 'https://www.cos.com/images/product/standard/24860315_50.jpg' WHERE name = 'COS Skirt';
UPDATE products SET image_url = 'https://image.uniqlo.com/UQ/ST3/WesternCommon/imagesgoods/469851/item/goods_69_469851.jpg' WHERE name = 'Uniqlo T-shirt';

-- 🏠 家电类（4款）
UPDATE products SET image_url = 'https://www.dyson.com.cn/sites/g/files/niwpdw526/files/2024-01/V15_DetectUpright_Titanium_369.png' WHERE name = 'Dyson V15 Vacuum';
UPDATE products SET image_url = 'https://i01.appmifile.com/webfile/globalimg/products/pc/roborock-s10plus/kv-render.png' WHERE name = 'Xiaomi Robot Vacuum';
UPDATE products SET image_url = 'https://www.midea.com/global/web/product-washing-machines/WD-inverter/10kg.jpg' WHERE name = 'Midea Washing Machine';
UPDATE products SET image_url = 'https://www.gree.com/media/catalog/product/cache/1/image/1200x/17f82f742ffe127f42dca9de82fb58b1/2/0/2023new_gree_ac1.5p_1.jpg' WHERE name = 'Gree Air Conditioner';

-- 显示更新结果
SELECT 'Product image URLs updated successfully!' as result;

-- 验证更新结果
SELECT id, name, LEFT(image_url, 80) as updated_image 
FROM products 
WHERE status = 1 
ORDER BY id 
LIMIT 15; 