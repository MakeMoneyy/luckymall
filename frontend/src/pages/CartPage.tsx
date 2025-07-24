import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Table, 
  InputNumber, 
  Button, 
  Space, 
  Typography, 
  Image, 
  Empty,
  Checkbox,
  Row,
  Col,
  Divider,
  message,
  Select,
  Tooltip,
  Slider,
  InputProps
} from 'antd';
import { DeleteOutlined, ShoppingOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { RootState, AppDispatch } from '../store';
import { 
  updateQuantity, 
  removeFromCart, 
  clearCart,
  applyCoupon,
  applyPoints,
  loadCartAsync,
  updateQuantityAsync,
  removeFromCartAsync,
  clearCartAsync
} from '../store/slices/cartSlice';
import { CartService } from '../services/cartService';
import { Coupon } from '../types';

const { Title, Text } = Typography;
const { Option } = Select;

// 模拟优惠券数据
const mockCoupons: Coupon[] = [
  {
    id: 1,
    name: '满100减10',
    description: '订单满100元减10元',
    type: 'FIXED',
    value: 10,
    minAmount: 100,
    startDate: '2023-01-01',
    endDate: '2025-12-31',
    status: 1
  },
  {
    id: 2,
    name: '满200减25',
    description: '订单满200元减25元',
    type: 'FIXED',
    value: 25,
    minAmount: 200,
    startDate: '2023-01-01',
    endDate: '2025-12-31',
    status: 1
  },
  {
    id: 3,
    name: '8折优惠',
    description: '订单满300元享8折优惠',
    type: 'DISCOUNT',
    value: 8,
    minAmount: 300,
    startDate: '2023-01-01',
    endDate: '2025-12-31',
    status: 1
  }
];

// 模拟用户ID
const MOCK_USER_ID = 1;

const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  
  const { items, totalAmount, coupon, pointsUsed, discountAmount, actualAmount } = useSelector(
    (state: RootState) => state.cart
  );
  
  // 本地状态管理选中的商品
  const [selectedItems, setSelectedItems] = useState<number[]>([]);
  
  // 模拟用户积分
  const [userPoints, setUserPoints] = useState<number>(1000);
  
  // 可用的优惠券列表
  const [availableCoupons, setAvailableCoupons] = useState<Coupon[]>([]);
  
  // 加载状态
  const [loading, setLoading] = useState<boolean>(false);
  
  // 加载购物车数据
  useEffect(() => {
    const fetchCartItems = async () => {
      try {
        setLoading(true);
        // 从后端加载购物车数据
        await dispatch(loadCartAsync()).unwrap();
      } catch (error) {
        console.error('获取购物车数据失败:', error);
        message.error('获取购物车数据失败');
      } finally {
        setLoading(false);
      }
    };
    
    fetchCartItems();
  }, [dispatch]);
  
  // 根据购物车金额筛选可用优惠券
  useEffect(() => {
    const filtered = mockCoupons.filter(c => c.minAmount <= totalAmount);
    setAvailableCoupons(filtered);
    
    // 如果当前选择的优惠券不再适用，则清除
    if (coupon && coupon.minAmount > totalAmount) {
      dispatch(applyCoupon(null));
    }
  }, [totalAmount, coupon, dispatch]);
  
  // 处理数量变化
  const handleQuantityChange = async (productId: number, quantity: number) => {
    if (quantity > 0) {
      try {
        console.log('更新购物车数量:', productId, quantity);
        
        // 先直接更新本地状态，提高响应速度
      dispatch(updateQuantity({ productId, quantity }));
        
        // 然后调用异步action
        await dispatch(updateQuantityAsync({ productId, quantity })).unwrap();
      } catch (error) {
        console.error('更新购物车数量失败:', error);
        message.error('更新购物车数量失败');
        
        // 如果失败，重新加载购物车数据
        dispatch(loadCartAsync());
      }
    }
  };
  
  // 删除商品
  const handleRemoveItem = async (productId: number) => {
    try {
      // 调用异步action
      await dispatch(removeFromCartAsync(productId)).unwrap();
    message.success('商品已从购物车中移除');
    } catch (error) {
      console.error('从购物车移除商品失败:', error);
      message.error('从购物车移除商品失败');
    }
  };
  
  // 清空购物车
  const handleClearCart = async () => {
    try {
      // 调用异步action
      await dispatch(clearCartAsync()).unwrap();
    message.success('购物车已清空');
    } catch (error) {
      console.error('清空购物车失败:', error);
      message.error('清空购物车失败');
    }
  };
  
  // 切换商品选择状态
  const handleToggleSelection = (productId: number) => {
    setSelectedItems(prev => 
      prev.includes(productId) 
        ? prev.filter(id => id !== productId)
        : [...prev, productId]
    );
  };
  
  // 全选/取消全选
  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedItems(items.map(item => item.product.id));
    } else {
      setSelectedItems([]);
    }
  };
  
  // 选择优惠券
  const handleCouponChange = (couponId: number | null) => {
    if (couponId === null) {
      dispatch(applyCoupon(null));
    } else {
      const selectedCoupon = mockCoupons.find(c => c.id === couponId);
      if (selectedCoupon) {
        dispatch(applyCoupon(selectedCoupon));
      }
    }
  };
  
  // 使用积分
  const handlePointsChange = (value: number | null) => {
    // 限制使用的积分不超过用户拥有的积分
    const pointsValue = value || 0;
    const limitedValue = Math.min(pointsValue, userPoints);
    // 限制使用的积分不超过订单金额的一半（按100积分=1元计算）
    const maxPointsAllowed = Math.floor(totalAmount * 50);
    const finalValue = Math.min(limitedValue, maxPointsAllowed);
    
    // 更新Redux状态
    dispatch(applyPoints(finalValue));
    
    // 这里可以调用后端API保存积分使用情况，但目前后端可能没有对应接口
    // 所以这部分暂时不实现
  };
  
  // 结算
  const handleCheckout = () => {
    const selectedCartItems = items.filter(item => 
      selectedItems.includes(item.product.id)
    );
    
    if (selectedCartItems.length === 0) {
      message.warning('请选择要结算的商品');
      return;
    }
    
    // 获取选中商品的购物车项ID，用于创建订单
    const cartItemIds = selectedCartItems.map(item => item.product.id); // 实际应用中应该使用真实的购物车项ID
    
    // 跳转到订单确认页面
    navigate('/order', { 
      state: { 
        cartItemIds,
        selectedItems: selectedCartItems,
        coupon,
        pointsUsed,
        discountAmount,
        actualAmount: calculateSelectedTotal() - (discountAmount || 0)
      } 
    });
  };
  
  // 计算选中商品的总价
  const calculateSelectedTotal = () => {
    return items
      .filter(item => selectedItems.includes(item.product.id))
      .reduce((total, item) => total + item.product.price * item.quantity, 0);
  };
  
  // 表格列定义
  const columns = [
    {
      title: (
        <Checkbox
          checked={selectedItems.length === items.length && items.length > 0}
          indeterminate={selectedItems.length > 0 && selectedItems.length < items.length}
          onChange={(e) => handleSelectAll(e.target.checked)}
        >
          全选
        </Checkbox>
      ),
      dataIndex: 'selection',
      key: 'selection',
      width: 80,
      render: (_: any, record: any) => (
        <Checkbox
          checked={selectedItems.includes(record.product.id)}
          onChange={() => handleToggleSelection(record.product.id)}
        />
      ),
    },
    {
      title: '商品信息',
      dataIndex: 'product',
      key: 'product',
      width: 300,
      render: (product: any) => (
        <Space>
          <Image
            width={60}
            height={60}
            src={product.imageUrl || 'https://via.placeholder.com/60x60?text=暂无图片'}
            alt={product.name}
            style={{ objectFit: 'cover' }}
          />
          <div>
            <div style={{ fontWeight: 500, marginBottom: 4 }}>
              {product.name}
            </div>
            <Text type="secondary" style={{ fontSize: 12 }}>
              {product.categoryName}
            </Text>
          </div>
        </Space>
      ),
    },
    {
      title: '单价',
      dataIndex: 'product',
      key: 'price',
      width: 120,
      render: (product: any) => (
        <Text strong style={{ color: '#ff4d4f' }}>
          ¥{product.price}
        </Text>
      ),
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 120,
      render: (quantity: number, record: any) => (
        <InputNumber
          min={1}
          max={record.product.stockQuantity}
          value={quantity}
          onChange={(value) => handleQuantityChange(record.product.id, value || 1)}
          addonBefore={<Button size="small" onClick={() => handleQuantityChange(record.product.id, quantity - 1)} disabled={quantity <= 1}>-</Button>}
          addonAfter={<Button size="small" onClick={() => handleQuantityChange(record.product.id, quantity + 1)} disabled={quantity >= record.product.stockQuantity}>+</Button>}
          style={{ width: 120 }}
        />
      ),
    },
    {
      title: '小计',
      key: 'subtotal',
      width: 120,
      render: (_: any, record: any) => (
        <Text strong style={{ color: '#ff4d4f' }}>
          ¥{(record.product.price * record.quantity).toFixed(2)}
        </Text>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: any, record: any) => (
        <Button
          type="link"
          danger
          icon={<DeleteOutlined />}
          onClick={() => handleRemoveItem(record.product.id)}
        >
          删除
        </Button>
      ),
    },
  ];
  
  if (items.length === 0) {
    return (
      <div style={{ padding: '20px' }}>
        <Title level={2}>购物车</Title>
        <Empty
          image={<ShoppingOutlined style={{ fontSize: 64, color: '#ccc' }} />}
          description="购物车是空的"
          style={{ padding: '60px 0' }}
        >
          <Button type="primary" onClick={() => navigate('/')}>
            去购物
          </Button>
        </Empty>
      </div>
    );
  }
  
  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>购物车</Title>
      
      <Card style={{ marginBottom: 20 }}>
        <Table
          columns={columns}
          dataSource={items}
          rowKey={(record) => record.product.id}
          pagination={false}
          size="middle"
        />
      </Card>
      
      <Row gutter={20}>
        <Col xs={24} md={16}>
          <Space>
            <Button onClick={handleClearCart} disabled={items.length === 0}>
              清空购物车
            </Button>
            <Button onClick={() => navigate('/')}>
              继续购物
            </Button>
          </Space>
        </Col>
        
        <Col xs={24} md={8}>
          <Card title="结算信息">
            <Space direction="vertical" style={{ width: '100%' }}>
              <Row justify="space-between">
                <Text>已选商品：</Text>
                <Text>{selectedItems.length} 件</Text>
              </Row>
              
              <Row justify="space-between">
                <Text>商品总价：</Text>
                <Text strong style={{ color: '#ff4d4f' }}>
                  ¥{calculateSelectedTotal().toFixed(2)}
                </Text>
              </Row>
              
              {/* 优惠券选择 */}
              <Row align="middle" style={{ marginTop: 10 }}>
                <Col span={8}>
                  <Text>优惠券：</Text>
                </Col>
                <Col span={16}>
                  <Select
                    style={{ width: '100%' }}
                    placeholder="请选择优惠券"
                    value={coupon?.id || null}
                    onChange={handleCouponChange}
                    allowClear
                  >
                    {availableCoupons.map(c => (
                      <Option key={c.id} value={c.id}>
                        {c.name} ({c.description})
                      </Option>
                    ))}
                  </Select>
                </Col>
              </Row>
              
              {/* 积分抵扣 */}
              <Row align="middle" style={{ marginTop: 10 }}>
                <Col span={8}>
                  <Space>
                    <Text>积分抵扣：</Text>
                    <Tooltip title="100积分=1元">
                      <QuestionCircleOutlined />
                    </Tooltip>
                  </Space>
                </Col>
                <Col span={16}>
                  <Row>
                    <Col span={16}>
                      <Slider
                        min={0}
                        max={userPoints}
                        step={100}
                        value={pointsUsed}
                        onChange={handlePointsChange}
                      />
                    </Col>
                    <Col span={8}>
                      <InputNumber
                        min={0}
                        max={userPoints}
                        step={100}
                        value={pointsUsed}
                        onChange={handlePointsChange}
                        style={{ marginLeft: 16, width: '100%' }}
                      />
                    </Col>
                  </Row>
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    您有 {userPoints} 积分可用，当前使用 {pointsUsed} 积分，抵扣 ¥{(pointsUsed / 100).toFixed(2)}
                  </Text>
                </Col>
              </Row>
              
              {/* 优惠金额 */}
              {discountAmount > 0 && (
                <Row justify="space-between" style={{ marginTop: 10 }}>
                  <Text>优惠金额：</Text>
                  <Text style={{ color: '#52c41a' }}>
                    -¥{discountAmount.toFixed(2)}
                  </Text>
                </Row>
              )}
              
              <Divider style={{ margin: '12px 0' }} />
              
              {/* 应付总额 */}
              <Row justify="space-between">
                <Text strong>应付总额：</Text>
                <Text strong style={{ color: '#ff4d4f', fontSize: 18 }}>
                  ¥{(calculateSelectedTotal() - (discountAmount || 0)).toFixed(2)}
                </Text>
              </Row>
              
              <Button 
                type="primary" 
                size="large" 
                block
                onClick={handleCheckout}
                disabled={selectedItems.length === 0}
              >
                结算 ({selectedItems.length})
              </Button>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default CartPage; 