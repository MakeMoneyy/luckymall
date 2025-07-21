import React, { useEffect, useState } from 'react';
import { 
  Card, 
  Row, 
  Col, 
  Pagination, 
  Spin, 
  Alert, 
  Select, 
  Button, 
  Space, 
  Typography,
  Image,
  Tag,
  InputNumber,
  message
} from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { RootState, AppDispatch } from '../store';
import { fetchProducts, setFilters, clearFilters } from '../store/slices/productSlice';
import { fetchAllCategories } from '../store/slices/categorySlice';
import { addToCartAsync } from '../store/slices/cartSlice';
import { Product } from '../types';

const { Title, Text } = Typography;
const { Option } = Select;

const ProductList: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { 
    products, 
    total, 
    current, 
    size, 
    loading, 
    error, 
    filters 
  } = useSelector((state: RootState) => state.products);
  
  const { allCategories } = useSelector((state: RootState) => state.categories);
  
  // 添加购物车加载状态
  const [addingToCart, setAddingToCart] = useState<Record<number, boolean>>({});
  
  useEffect(() => {
    // 初始化时获取商品列表和分类
    dispatch(fetchProducts({ current: 1, size: 12 }));
    dispatch(fetchAllCategories());
  }, [dispatch]);
  
  // 处理分页变化
  const handlePageChange = (page: number, pageSize?: number) => {
    dispatch(fetchProducts({ 
      current: page, 
      size: pageSize || size,
      ...filters 
    }));
  };
  
  // 处理分类筛选
  const handleCategoryChange = (categoryId: number | undefined) => {
    const newFilters = { ...filters, categoryId };
    dispatch(setFilters(newFilters));
    dispatch(fetchProducts({ 
      current: 1, 
      size,
      ...newFilters 
    }));
  };
  
  // 处理价格筛选
  const handleMinPriceChange = (value: number | null) => {
    const newFilters = { ...filters, minPrice: value || undefined };
    dispatch(setFilters(newFilters));
    dispatch(fetchProducts({ 
      current: 1, 
      size,
      ...newFilters 
    }));
  };
  
  const handleMaxPriceChange = (value: number | null) => {
    const newFilters = { ...filters, maxPrice: value || undefined };
    dispatch(setFilters(newFilters));
    dispatch(fetchProducts({ 
      current: 1, 
      size,
      ...newFilters 
    }));
  };
  
  // 处理排序变化
  const handleSortChange = (sortBy: string) => {
    const newFilters = { ...filters, sortBy };
    dispatch(setFilters(newFilters));
    dispatch(fetchProducts({ 
      current: 1, 
      size,
      ...newFilters 
    }));
  };
  
  // 清除所有筛选条件
  const handleClearFilters = () => {
    dispatch(clearFilters());
    dispatch(fetchProducts({ current: 1, size }));
  };
  
  // 跳转到商品详情页
  const handleProductClick = (productId: number) => {
    navigate(`/product/${productId}`);
  };
  
  // 添加到购物车
  const handleAddToCart = async (e: React.MouseEvent, product: Product) => {
    e.stopPropagation(); // 阻止冒泡，避免触发卡片点击事件
    
    try {
      setAddingToCart(prev => ({ ...prev, [product.id]: true }));
      
      // 调用异步action添加到购物车
      await dispatch(addToCartAsync({ product, quantity: 1 })).unwrap();
      message.success(`已将"${product.name}"添加到购物车`);
    } catch (error) {
      console.error('添加到购物车失败:', error);
      message.error('添加到购物车失败，请稍后重试');
    } finally {
      setAddingToCart(prev => ({ ...prev, [product.id]: false }));
    }
  };
  
  // 渲染商品卡片
  const renderProductCard = (product: Product) => (
    <Col xs={24} sm={12} md={8} lg={6} key={product.id}>
      <Card
        hoverable
        style={{ marginBottom: 16 }}
        onClick={() => handleProductClick(product.id)}
        cover={
          <Image
            alt={product.name}
            src={product.imageUrl || 'https://via.placeholder.com/300x300?text=暂无图片'}
            height={200}
            style={{ objectFit: 'cover' }}
            preview={false}
          />
        }
        actions={[
          <Button 
            type="primary" 
            size="small" 
            key="detail"
            onClick={(e) => {
              e.stopPropagation();
              handleProductClick(product.id);
            }}
          >
            查看详情
          </Button>,
          <Button 
            size="small" 
            key="cart"
            icon={<ShoppingCartOutlined />}
            onClick={(e) => handleAddToCart(e, product)}
            loading={addingToCart[product.id]}
            disabled={product.stockQuantity <= 0 || addingToCart[product.id]}
          >
            加入购物车
          </Button>
        ]}
      >
        <Card.Meta
          title={
            <div style={{ height: 44, overflow: 'hidden' }}>
              <Text ellipsis={{ tooltip: product.name }}>
                {product.name}
              </Text>
            </div>
          }
          description={
            <div>
              <div style={{ marginBottom: 8 }}>
                <Text strong style={{ color: '#ff4d4f', fontSize: 18 }}>
                  ¥{product.price}
                </Text>
              </div>
              <div style={{ marginBottom: 4 }}>
                <Text type="secondary">库存: {product.stockQuantity}</Text>
              </div>
              <div style={{ marginBottom: 4 }}>
                <Text type="secondary">销量: {product.salesCount}</Text>
              </div>
              {product.categoryName && (
                <Tag color="blue">{product.categoryName}</Tag>
              )}
            </div>
          }
        />
      </Card>
    </Col>
  );
  
  if (error) {
    return (
      <Alert
        message="加载失败"
        description={error}
        type="error"
        showIcon
        action={
          <Button size="small" danger onClick={() => dispatch(fetchProducts({ current: 1, size }))}>
            重试
          </Button>
        }
      />
    );
  }
  
  return (
    <div style={{ padding: '20px' }}>
      {/* 页面标题 */}
      <Title level={2}>商品列表</Title>
      
      {/* 筛选条件 */}
      <Card style={{ marginBottom: 20 }}>
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <Space wrap>
            <div>
              <Text strong>分类筛选：</Text>
              <Select
                placeholder="选择分类"
                style={{ width: 200, marginLeft: 8 }}
                allowClear
                value={filters.categoryId}
                onChange={handleCategoryChange}
              >
                {allCategories.map(category => (
                  <Option key={category.id} value={category.id}>
                    {category.name}
                  </Option>
                ))}
              </Select>
            </div>
            
            <div>
              <Text strong>排序方式：</Text>
              <Select
                style={{ width: 150, marginLeft: 8 }}
                placeholder="选择排序"
                value={filters.sortBy}
                onChange={handleSortChange}
              >
                <Option value="price_asc">价格从低到高</Option>
                <Option value="price_desc">价格从高到低</Option>
                <Option value="sales_desc">销量从高到低</Option>
                <Option value="created_desc">最新发布</Option>
              </Select>
            </div>
            
            <Button onClick={handleClearFilters}>
              清除筛选
            </Button>
          </Space>
          
          <Space wrap>
            <Text strong>价格范围：</Text>
            <InputNumber
              placeholder="最低价格"
              style={{ width: 120 }}
              min={0}
              value={filters.minPrice}
              onChange={handleMinPriceChange}
            />
            <Text>至</Text>
            <InputNumber
              placeholder="最高价格"
              style={{ width: 120 }}
              min={0}
              value={filters.maxPrice}
              onChange={handleMaxPriceChange}
            />
          </Space>
        </Space>
      </Card>
      
      {/* 商品列表 */}
      <Spin spinning={loading}>
        <Row gutter={[16, 16]}>
          {products.map(renderProductCard)}
        </Row>
        
        {products.length === 0 && !loading && (
          <div style={{ textAlign: 'center', padding: '40px 0' }}>
            <Text type="secondary">暂无商品数据</Text>
          </div>
        )}
      </Spin>
      
      {/* 分页 */}
      {total > 0 && (
        <div style={{ textAlign: 'center', marginTop: 32 }}>
          <Pagination
            current={current}
            total={total}
            pageSize={size}
            showSizeChanger
            showQuickJumper
            showTotal={(total, range) => 
              `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
            }
            onChange={handlePageChange}
            onShowSizeChange={handlePageChange}
          />
        </div>
      )}
    </div>
  );
};

export default ProductList; 