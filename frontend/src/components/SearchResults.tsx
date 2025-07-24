import React, { useEffect } from 'react';
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
  Empty
} from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { RootState, AppDispatch } from '../store';
import { searchProducts, resetSearch } from '../store/slices/productSlice';
import { Product } from '../types';

const { Title, Text } = Typography;
const { Option } = Select;

const SearchResults: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  
  const { 
    searchResults, 
    searchTotal, 
    searchCurrent, 
    searchLoading, 
    searchError,
    lastSearchKeyword 
  } = useSelector((state: RootState) => state.products);
  
  const keyword = searchParams.get('keyword') || '';
  
  useEffect(() => {
    if (keyword && keyword !== lastSearchKeyword) {
      dispatch(searchProducts({ 
        keyword,
        current: 1,
        size: 12 
      }));
    }
    
    // 组件卸载时重置搜索结果
    return () => {
      dispatch(resetSearch());
    };
  }, [dispatch, keyword, lastSearchKeyword]);
  
  // 处理分页变化
  const handlePageChange = (page: number, pageSize?: number) => {
    dispatch(searchProducts({ 
      keyword,
      current: page, 
      size: pageSize || 12
    }));
  };
  
  // 处理排序变化
  const handleSortChange = (sortBy: string) => {
    dispatch(searchProducts({ 
      keyword,
      current: 1, 
      size: 12,
      sortBy
    }));
  };
  
  // 跳转到商品详情页
  const handleProductClick = (productId: number) => {
    navigate(`/product/${productId}`);
  };
  
  // 渲染商品卡片
  const renderProductCard = (product: Product) => (
    <Col xs={24} sm={12} md={8} lg={6} key={product.id}>
      <Card
        hoverable
        style={{ marginBottom: 16 }}
        cover={
          <Image
            alt={product.name}
            src={product.imageUrl || 'https://via.placeholder.com/300x300?text=暂无图片'}
            height={200}
            style={{ objectFit: 'cover' }}
            preview={false}
          />
        }
        onClick={() => handleProductClick(product.id)}
        actions={[
          <Button type="primary" size="small" key="detail">
            查看详情
          </Button>,
          <Button size="small" key="cart">
            加入购物车
          </Button>
        ]}
      >
        <Card.Meta
          title={
            <div style={{ height: 44, overflow: 'hidden' }}>
              <Text ellipsis={{ tooltip: product.name }}>
                {/* 高亮搜索关键词 */}
                {highlightKeyword(product.name, keyword)}
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
  
  // 高亮关键词函数
  const highlightKeyword = (text: string, keyword: string) => {
    if (!keyword) return text;
    
    const regex = new RegExp(`(${keyword})`, 'gi');
    const parts = text.split(regex);
    
    return parts.map((part, index) => 
      regex.test(part) ? (
        <span key={index} style={{ backgroundColor: '#fff566' }}>
          {part}
        </span>
      ) : (
        part
      )
    );
  };
  
  if (searchError) {
    return (
      <div style={{ padding: '20px' }}>
        <Alert
          message="搜索失败"
          description={searchError}
          type="error"
          showIcon
          action={
            <Button size="small" danger onClick={() => dispatch(searchProducts({ keyword, current: 1, size: 12 }))}>
              重试
            </Button>
          }
        />
      </div>
    );
  }
  
  return (
    <div style={{ padding: '20px' }}>
      {/* 搜索结果头部 */}
      <div style={{ marginBottom: 20 }}>
        <Title level={3}>
          搜索结果："{keyword}"
        </Title>
        {!searchLoading && (
          <Text type="secondary">
            共找到 {searchTotal} 个相关商品
          </Text>
        )}
      </div>
      
      {/* 排序选项 */}
      {searchTotal > 0 && (
        <Card style={{ marginBottom: 20 }}>
          <Space>
            <Text strong>排序方式：</Text>
            <Select
              style={{ width: 150 }}
              placeholder="选择排序"
              onChange={handleSortChange}
            >
              <Option value="relevance">相关度</Option>
              <Option value="price_asc">价格从低到高</Option>
              <Option value="price_desc">价格从高到低</Option>
              <Option value="sales_desc">销量从高到低</Option>
              <Option value="created_desc">最新发布</Option>
            </Select>
          </Space>
        </Card>
      )}
      
      {/* 搜索结果列表 */}
      <Spin spinning={searchLoading}>
        {searchResults.length > 0 ? (
          <Row gutter={[16, 16]}>
            {searchResults.map(renderProductCard)}
          </Row>
        ) : !searchLoading && (
          <Empty
            description={
              <span>
                没有找到相关商品<br />
                请尝试其他关键词
              </span>
            }
            style={{ padding: '60px 0' }}
          >
            <Button type="primary" onClick={() => navigate('/')}>
              返回首页
            </Button>
          </Empty>
        )}
      </Spin>
      
      {/* 分页 */}
      {searchTotal > 0 && (
        <div style={{ textAlign: 'center', marginTop: 32 }}>
          <Pagination
            current={searchCurrent}
            total={searchTotal}
            pageSize={12}
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

export default SearchResults; 