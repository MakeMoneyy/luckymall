import React, { useEffect } from 'react';
import { 
  Row, 
  Col, 
  Card, 
  Typography, 
  Button, 
  Carousel, 
  Space, 
  Divider,
  List,
  Image,
  Tag,
  Spin,
  Avatar
} from 'antd';
import { 
  ShoppingOutlined, 
  TagsOutlined, 
  FireOutlined,
  RightOutlined,
  NotificationOutlined,
  SoundOutlined,
  ThunderboltOutlined,
  ShoppingCartOutlined,
  LeftOutlined,
  EyeOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../store';
import { fetchProducts } from '../store/slices/productSlice';
import { fetchAllCategories } from '../store/slices/categorySlice';
import '../styles/HomePage.css';

const { Title, Paragraph, Text } = Typography;
const { Meta } = Card;

// 默认热门商品数据，在API数据加载前显示
const defaultHotProducts = [
  { 
    id: 1, 
    name: "iPhone 14 Pro Max", 
    price: 8999, 
    description: "苹果最新旗舰手机", 
    imageUrl: "https://via.placeholder.com/400x300?text=iPhone" 
  },
  { 
    id: 2, 
    name: "MacBook Air M2", 
    price: 7999, 
    description: "轻薄便携的笔记本电脑", 
    imageUrl: "https://via.placeholder.com/400x300?text=MacBook" 
  },
  { 
    id: 3, 
    name: "AirPods Pro", 
    price: 1999, 
    description: "主动降噪无线耳机", 
    imageUrl: "https://via.placeholder.com/400x300?text=AirPods" 
  },
  { 
    id: 4, 
    name: "Nike Air Jordan", 
    price: 1299, 
    description: "经典篮球鞋", 
    imageUrl: "https://via.placeholder.com/400x300?text=Nike" 
  },
  { 
    id: 5, 
    name: "索尼WH-1000XM5", 
    price: 2899, 
    description: "顶级降噪耳机", 
    imageUrl: "https://via.placeholder.com/400x300?text=Sony" 
  },
  { 
    id: 6, 
    name: "三星Galaxy S23", 
    price: 6999, 
    description: "安卓旗舰手机", 
    imageUrl: "https://via.placeholder.com/400x300?text=Samsung" 
  }
];

// 自定义CSS样式
const carouselStyle = `
  .hot-products-carousel .slick-track {
    display: flex !important;
  }
  .hot-products-carousel .slick-slide {
    height: inherit !important;
    display: flex !important;
  }
  .hot-products-carousel .slick-slide > div {
    display: flex;
    width: 100%;
  }
  .product-card-large {
    position: relative;
    overflow: hidden;
    border-radius: 8px;
  }
  .product-card-large .product-info {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0,0,0,0.6);
    color: white;
    padding: 16px;
    transition: all 0.3s ease;
  }
  .product-card-large:hover .product-info {
    background: rgba(0,0,0,0.8);
  }
  .product-card-large .product-img {
    transition: transform 0.5s ease;
    width: 100%;
    height: 300px;
    object-fit: cover;
  }
  .product-card-large:hover .product-img {
    transform: scale(1.05);
  }
  .product-card-large .product-tag {
    position: absolute;
    top: 16px;
    right: 16px;
  }
  .product-card-large .product-action {
    position: absolute;
    top: 16px;
    left: 16px;
    opacity: 0;
    transition: opacity 0.3s ease;
  }
  .product-card-large:hover .product-action {
    opacity: 1;
  }
`;

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  
  const { products, loading } = useSelector((state: RootState) => state.products);
  const { allCategories } = useSelector((state: RootState) => state.categories);
  
  useEffect(() => {
    // 获取推荐商品和分类
    dispatch(fetchProducts({ current: 1, size: 12 }));
    dispatch(fetchAllCategories());
  }, [dispatch]);
  
  // 轮播图数据
  const bannerImages = [
    {
      id: 1,
      imageUrl: 'https://via.placeholder.com/1200x400?text=招财商城+促销活动',
      title: '夏季大促',
      link: '/search?keyword=夏季'
    },
    {
      id: 2,
      imageUrl: 'https://via.placeholder.com/1200x400?text=新品上市',
      title: '新品上市',
      link: '/search?keyword=新品'
    },
    {
      id: 3,
      imageUrl: 'https://via.placeholder.com/1200x400?text=限时折扣',
      title: '限时折扣',
      link: '/search?keyword=折扣'
    }
  ];
  
  // 热门商品数据（从products中获取，如果为空则使用默认数据）
  const hotProducts = products && products.length > 0 ? products.slice(0, 6) : defaultHotProducts;
  
  // 将热门商品分组，每组3个
  const groupedHotProducts = [];
  for (let i = 0; i < hotProducts.length; i += 3) {
    groupedHotProducts.push(hotProducts.slice(i, i + 3));
  }
  
  return (
    <div style={{ padding: '24px' }}>
      {/* 注入自定义样式 */}
      <style>{carouselStyle}</style>
      
      {/* 轮播图 */}
      <Card bordered={false} style={{ marginBottom: 24 }}>
        <Carousel autoplay effect="fade">
          {bannerImages.map(banner => (
            <div key={banner.id} onClick={() => navigate(banner.link)} style={{ cursor: 'pointer' }}>
              <img 
                src={banner.imageUrl} 
                alt={banner.title} 
                style={{ width: '100%', height: 'auto', maxHeight: '400px', objectFit: 'cover' }}
              />
            </div>
          ))}
        </Carousel>
      </Card>
      
      {/* 热门商品大图滚动展示 */}
      <Card 
        title={
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <FireOutlined style={{ color: '#ff4d4f', marginRight: 8 }} />
            <span>热门商品</span>
          </div>
        }
        extra={
          <Button type="link" onClick={() => navigate('/products')}>
            查看更多 <RightOutlined />
          </Button>
        }
        bordered={false}
        style={{ marginBottom: 24 }}
      >
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px 0' }}>
            <Spin size="large" />
            <div style={{ marginTop: 16 }}>
              <Text type="secondary">加载热门商品中...</Text>
            </div>
          </div>
        ) : (
          <div className="hot-products-carousel">
            <Carousel 
              autoplay 
              autoplaySpeed={5000}
              dots={{ className: 'carousel-dots' }}
              arrows
              slidesToShow={1}
              slidesToScroll={1}
              prevArrow={<Button icon={<LeftOutlined />} shape="circle" />}
              nextArrow={<Button icon={<RightOutlined />} shape="circle" />}
            >
              {groupedHotProducts.map((group, groupIndex) => (
                <div key={groupIndex}>
                  <Row gutter={16}>
                    {group.map(product => (
                      <Col span={8} key={product.id}>
                        <div 
                          className="product-card-large"
                          onClick={() => navigate(`/product/${product.id}`)}
                        >
                          <img 
                            className="product-img"
                            src={product.imageUrl || 'https://via.placeholder.com/400x300?text=商品图片'} 
                            alt={product.name} 
                          />
                          <div className="product-tag">
                            <Tag color="red" style={{ fontSize: '14px', padding: '2px 8px' }}>
                              热销
                            </Tag>
                          </div>
                          <div className="product-action">
                            <Button 
                              type="primary" 
                              shape="circle" 
                              icon={<EyeOutlined />} 
                              size="large"
                              onClick={(e) => {
                                e.stopPropagation();
                                navigate(`/product/${product.id}`);
                              }}
                            />
                          </div>
                          <div className="product-info">
                            <Title level={5} style={{ color: 'white', margin: 0, marginBottom: 8 }}>
                              {product.name}
                            </Title>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                              <Text style={{ color: '#ff4d4f', fontSize: '18px', fontWeight: 'bold' }}>
                                ¥{product.price?.toFixed(2)}
                              </Text>
                              <Button 
                                type="primary" 
                                icon={<ShoppingCartOutlined />}
                                onClick={(e) => {
                                  e.stopPropagation();
                                  // 这里可以添加加入购物车的逻辑
                                }}
                              >
                                加入购物车
                              </Button>
                            </div>
                          </div>
                        </div>
                      </Col>
                    ))}
                  </Row>
                </div>
              ))}
            </Carousel>
          </div>
        )}
      </Card>
      
      {/* 欢迎信息 */}
      <Card bordered={false} style={{ marginBottom: 24, textAlign: 'center' }}>
        <Title level={2} style={{ color: '#1890ff' }}>欢迎来到招财商城</Title>
        <Paragraph style={{ fontSize: 16 }}>
          我们提供优质的商品和卓越的服务，让您的购物体验更加愉悦！
        </Paragraph>
        <Button 
          type="primary" 
          size="large" 
          icon={<ShoppingOutlined />}
          onClick={() => navigate('/search')}
          style={{ margin: '16px 0' }}
        >
          开始购物
        </Button>
      </Card>
      
      {/* 商品分类 */}
      <div className="home-section">
        <div className="section-header">
          <Title level={4} className="section-title">
            <TagsOutlined /> 商品分类
          </Title>
          <Button type="link" onClick={() => navigate('/search')}>
            查看全部 <RightOutlined />
          </Button>
        </div>
        
        <Row gutter={[16, 16]}>
          {allCategories.slice(0, 8).map(category => (
            <Col xs={12} sm={8} md={6} lg={4} xl={3} key={category.id}>
              <Card
                hoverable
                style={{ textAlign: 'center' }}
                onClick={() => navigate(`/search?categoryId=${category.id}`)}
              >
                <div style={{ padding: '12px 0' }}>
                  <div style={{ 
                    fontSize: 36, 
                    color: '#1890ff',
                    marginBottom: 8
                  }}>
                    <TagsOutlined />
                  </div>
                  <div>{category.name}</div>
                </div>
              </Card>
            </Col>
          ))}
        </Row>
      </div>
      
      {/* 推荐商品 */}
      <div className="home-section">
        <div className="section-header">
          <Title level={4} className="section-title">
            <FireOutlined /> 热门推荐
          </Title>
          <Button type="link" onClick={() => navigate('/search')}>
            查看更多 <RightOutlined />
          </Button>
        </div>
        
        <Row gutter={[16, 16]}>
          {products.slice(0, 8).map(product => (
            <Col xs={24} sm={12} md={8} lg={6} key={product.id}>
              <Card
                hoverable
                className="product-card"
                cover={
                  <div style={{ height: 200, overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <img 
                      alt={product.name} 
                      src={product.imageUrl || 'https://via.placeholder.com/300x300?text=商品图片'} 
                      style={{ width: '100%', height: 'auto' }}
                    />
                  </div>
                }
                onClick={() => navigate(`/product/${product.id}`)}
              >
                <Meta
                  title={product.name}
                  description={
                    <div>
                      <div className="product-price">
                        ¥{product.price.toFixed(2)}
                      </div>
                      <div className="product-description">
                        {product.description?.substring(0, 40) || '暂无描述'}
                        {product.description?.length > 40 ? '...' : ''}
                      </div>
                    </div>
                  }
                />
              </Card>
            </Col>
          ))}
        </Row>
      </div>
      
      {/* 商城特点 */}
      <Card bordered={false} style={{ marginBottom: 24 }}>
        <Row gutter={24}>
          <Col span={8}>
            <div style={{ textAlign: 'center', padding: '24px 0' }}>
              <div style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }}>
                <ShoppingOutlined />
              </div>
              <Title level={4}>正品保障</Title>
              <Paragraph>所有商品均为正品，假一赔十</Paragraph>
            </div>
          </Col>
          <Col span={8}>
            <div style={{ textAlign: 'center', padding: '24px 0' }}>
              <div style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }}>
                <ShoppingOutlined />
              </div>
              <Title level={4}>极速配送</Title>
              <Paragraph>当日下单，次日送达</Paragraph>
            </div>
          </Col>
          <Col span={8}>
            <div style={{ textAlign: 'center', padding: '24px 0' }}>
              <div style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }}>
                <ShoppingOutlined />
              </div>
              <Title level={4}>无忧退换</Title>
              <Paragraph>7天无理由退换，售后无忧</Paragraph>
            </div>
          </Col>
        </Row>
      </Card>
    </div>
  );
};

export default HomePage; 