import React, { useEffect } from 'react';
import { 
  Card, 
  Row, 
  Col, 
  Button, 
  Typography, 
  Image, 
  Tag, 
  InputNumber, 
  Space, 
  Divider,
  Spin,
  Alert,
  Breadcrumb,
  message
} from 'antd';
import { ShoppingCartOutlined, HeartOutlined, HomeOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useNavigate } from 'react-router-dom';
import { RootState, AppDispatch } from '../store';
import { fetchProductById, resetCurrentProduct } from '../store/slices/productSlice';
import { addToCartAsync } from '../store/slices/cartSlice';

const { Title, Text, Paragraph } = Typography;

const ProductDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  
  const { 
    currentProduct, 
    detailLoading, 
    detailError 
  } = useSelector((state: RootState) => state.products);
  
  const [quantity, setQuantity] = React.useState(1);
  const [addingToCart, setAddingToCart] = React.useState(false);
  
  useEffect(() => {
    if (id) {
      dispatch(fetchProductById(Number(id)));
    }
    
    // 组件卸载时重置当前商品
    return () => {
      dispatch(resetCurrentProduct());
    };
  }, [dispatch, id]);
  
  const handleQuantityChange = (value: number | null) => {
    if (value && value > 0) {
      setQuantity(value);
    }
  };
  
  const handleAddToCart = async () => {
    if (currentProduct) {
      try {
        setAddingToCart(true);
        // 调用异步action
        await dispatch(addToCartAsync({ product: currentProduct, quantity })).unwrap();
        message.success(`已将${quantity}件"${currentProduct.name}"添加到购物车`);
      } catch (error) {
        message.error('添加到购物车失败，请稍后重试');
        console.error('添加到购物车失败:', error);
      } finally {
        setAddingToCart(false);
      }
    }
  };
  
  const handleBuyNow = async () => {
    if (currentProduct) {
      try {
        setAddingToCart(true);
        // 调用异步action
        await dispatch(addToCartAsync({ product: currentProduct, quantity })).unwrap();
        // 然后跳转到购物车页面
        navigate('/cart');
      } catch (error) {
        message.error('添加到购物车失败，请稍后重试');
        console.error('添加到购物车失败:', error);
      } finally {
        setAddingToCart(false);
      }
    }
  };
  
  if (detailError) {
    return (
      <div style={{ padding: '20px' }}>
        <Alert
          message="加载失败"
          description={detailError}
          type="error"
          showIcon
          action={
            <Space>
              <Button size="small" onClick={() => navigate('/')}>
                返回首页
              </Button>
              <Button size="small" danger onClick={() => id && dispatch(fetchProductById(Number(id)))}>
                重试
              </Button>
            </Space>
          }
        />
      </div>
    );
  }
  
  if (detailLoading || !currentProduct) {
    return (
      <div style={{ padding: '20px', textAlign: 'center' }}>
        <Spin size="large" />
      </div>
    );
  }
  
  return (
    <div style={{ padding: '20px' }}>
      {/* 面包屑导航 */}
      <Breadcrumb style={{ marginBottom: 20 }}>
        <Breadcrumb.Item>
          <HomeOutlined onClick={() => navigate('/')} style={{ cursor: 'pointer' }} />
        </Breadcrumb.Item>
        <Breadcrumb.Item>
          <span onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
            商品列表
          </span>
        </Breadcrumb.Item>
        {currentProduct.categoryName && (
          <Breadcrumb.Item>{currentProduct.categoryName}</Breadcrumb.Item>
        )}
        <Breadcrumb.Item>{currentProduct.name}</Breadcrumb.Item>
      </Breadcrumb>
      
      <Row gutter={[32, 32]}>
        {/* 商品图片区域 */}
        <Col xs={24} md={12}>
          <Card>
            <Image
              width="100%"
              height={400}
              src={currentProduct.imageUrl || 'https://via.placeholder.com/400x400?text=暂无图片'}
              alt={currentProduct.name}
              style={{ objectFit: 'cover' }}
            />
          </Card>
        </Col>
        
        {/* 商品信息区域 */}
        <Col xs={24} md={12}>
          <div>
            {/* 商品标题 */}
            <Title level={2}>{currentProduct.name}</Title>
            
            {/* 商品标签 */}
            <Space style={{ marginBottom: 16 }}>
              {currentProduct.categoryName && (
                <Tag color="blue">{currentProduct.categoryName}</Tag>
              )}
              {currentProduct.status === 1 ? (
                <Tag color="green">现货</Tag>
              ) : (
                <Tag color="red">缺货</Tag>
              )}
            </Space>
            
            {/* 价格信息 */}
            <Card style={{ marginBottom: 16 }}>
              <Space direction="vertical" size="small">
                <div>
                  <Text type="secondary">价格</Text>
                </div>
                <div>
                  <Text strong style={{ fontSize: 28, color: '#ff4d4f' }}>
                    ¥{currentProduct.price}
                  </Text>
                </div>
              </Space>
            </Card>
            
            {/* 库存和销量 */}
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={12}>
                <Card size="small">
                  <Text type="secondary">库存</Text>
                  <br />
                  <Text strong>{currentProduct.stockQuantity} 件</Text>
                </Card>
              </Col>
              <Col span={12}>
                <Card size="small">
                  <Text type="secondary">销量</Text>
                  <br />
                  <Text strong>{currentProduct.salesCount} 件</Text>
                </Card>
              </Col>
            </Row>
            
            {/* 购买数量选择 */}
            <Card style={{ marginBottom: 20 }}>
              <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                <div>
                  <Text strong>购买数量</Text>
                </div>
                <Space>
                  <InputNumber
                    min={1}
                    max={currentProduct.stockQuantity}
                    value={quantity}
                    onChange={handleQuantityChange}
                    style={{ width: 120 }}
                  />
                  <Text type="secondary">
                    (库存 {currentProduct.stockQuantity} 件)
                  </Text>
                </Space>
              </Space>
            </Card>
            
            {/* 操作按钮 */}
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
              <Space size="middle">
                <Button 
                  type="primary" 
                  size="large" 
                  danger
                  onClick={handleBuyNow}
                  disabled={currentProduct.stockQuantity === 0 || addingToCart}
                  style={{ minWidth: 120 }}
                >
                  立即购买
                </Button>
                <Button 
                  size="large" 
                  icon={<ShoppingCartOutlined />}
                  onClick={handleAddToCart}
                  disabled={currentProduct.stockQuantity === 0 || addingToCart}
                  style={{ minWidth: 120 }}
                >
                  加入购物车
                </Button>
                <Button 
                  size="large" 
                  icon={<HeartOutlined />}
                  style={{ minWidth: 100 }}
                >
                  收藏
                </Button>
              </Space>
              
              {currentProduct.stockQuantity === 0 && (
                <Alert
                  message="商品缺货"
                  description="该商品暂时缺货，请关注补货信息"
                  type="warning"
                  showIcon
                />
              )}
            </Space>
          </div>
        </Col>
      </Row>
      
      <Divider />
      
      {/* 商品详情描述 */}
      <Row>
        <Col span={24}>
          <Card title="商品详情">
            {currentProduct.description ? (
              <Paragraph>
                {currentProduct.description}
              </Paragraph>
            ) : (
              <Text type="secondary">暂无商品详情描述</Text>
            )}
          </Card>
        </Col>
      </Row>
      
      <Divider />
      
      {/* 用户评价区域（模拟展示） */}
      <Row>
        <Col span={24}>
          <Card title="用户评价">
            <div style={{ textAlign: 'center', padding: '40px 0' }}>
              <Text type="secondary">暂无用户评价</Text>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ProductDetail; 