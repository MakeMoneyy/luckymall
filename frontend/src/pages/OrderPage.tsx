import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Typography, 
  Button, 
  Space, 
  Row, 
  Col, 
  Divider, 
  Radio, 
  Input, 
  message,
  Table,
  Image,
  Form,
  Modal,
  Select
} from 'antd';
import { 
  EditOutlined, 
  PlusOutlined, 
  CreditCardOutlined, 
  BankOutlined, 
  WalletOutlined,
  DeleteOutlined,
  AlipayCircleOutlined,
  WechatOutlined
} from '@ant-design/icons';
import { useLocation, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState, AppDispatch } from '../store';
import { clearCart } from '../store/slices/cartSlice';
import { createOrderAsync, addLocalOrder } from '../store/slices/orderSlice';
import { CartItem, Coupon, UserAddress, UserCreditCard, InstallmentPlan } from '../types';
import { OrderService, CreateOrderRequest } from '../services/orderService';
import { PaymentService } from '../services/paymentService';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;

// 模拟用户地址数据
const mockAddresses: UserAddress[] = [
  {
    id: 1,
    userId: 1,
    receiverName: '张三',
    phone: '13800138000',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detailAddress: '科技园南路XX号XX大厦XX室',
    postalCode: '518000',
    isDefault: true
  },
  {
    id: 2,
    userId: 1,
    receiverName: '李四',
    phone: '13900139000',
    province: '广东省',
    city: '广州市',
    district: '天河区',
    detailAddress: '天河路XX号XX公寓XX室',
    postalCode: '510000',
    isDefault: false
  }
];

// 模拟用户信用卡数据
const mockCreditCards: UserCreditCard[] = [
  {
    id: 1,
    userId: 1,
    cardNumber: '6225 **** **** 8888',
    cardType: 'VISA',
    expiryDate: '12/25',
    isDefault: true
  },
  {
    id: 2,
    userId: 1,
    cardNumber: '6222 **** **** 9999',
    cardType: 'MasterCard',
    expiryDate: '09/24',
    isDefault: false
  }
];

// 模拟分期方案数据
const mockInstallmentPlans: InstallmentPlan[] = [
  {
    id: 1,
    planName: '3期免息',
    installmentCount: 3,
    interestRate: 0,
    minAmount: 500,
    maxAmount: 50000,
    status: 1
  },
  {
    id: 2,
    planName: '6期免息',
    installmentCount: 6,
    interestRate: 0,
    minAmount: 1000,
    maxAmount: 30000,
    status: 1
  },
  {
    id: 3,
    planName: '12期免息',
    installmentCount: 12,
    interestRate: 0,
    minAmount: 2000,
    maxAmount: 20000,
    status: 1
  },
  {
    id: 4,
    planName: '24期',
    installmentCount: 24,
    interestRate: 0.05, // 5%年利率
    minAmount: 5000,
    maxAmount: 50000,
    status: 1
  },
  {
    id: 5,
    planName: '36期',
    installmentCount: 36,
    interestRate: 0.06, // 6%年利率
    minAmount: 10000,
    maxAmount: 100000,
    status: 1
  }
];

// 模拟用户ID
const MOCK_USER_ID = 1;

interface OrderPageState {
  cartItemIds: number[];
  selectedItems: CartItem[];
  coupon: Coupon | null;
  pointsUsed: number;
  discountAmount: number;
  actualAmount: number;
}

const OrderPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  
  // 从路由状态中获取数据
  const state = location.state as OrderPageState;
  
  // 如果没有传递状态，重定向到首页
  useEffect(() => {
    if (!state || !state.selectedItems || state.selectedItems.length === 0) {
      navigate('/');
      message.error('订单信息不完整');
    }
  }, [state, navigate]);
  
  // 订单状态
  const [selectedAddress, setSelectedAddress] = useState<UserAddress | null>(mockAddresses.find(a => a.isDefault) || null);
  const [paymentMethod, setPaymentMethod] = useState<string>('credit_card');
  const [selectedCard, setSelectedCard] = useState<UserCreditCard | null>(mockCreditCards.find(c => c.isDefault) || null);
  const [installmentPlan, setInstallmentPlan] = useState<InstallmentPlan | null>(null);
  const [remark, setRemark] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  
  // 模态框状态
  const [addressModalVisible, setAddressModalVisible] = useState<boolean>(false);
  const [cardModalVisible, setCardModalVisible] = useState<boolean>(false);
  
  // Form实例
  const [form] = Form.useForm();
  
  // 可用的分期方案
  const [availableInstallmentPlans, setAvailableInstallmentPlans] = useState<InstallmentPlan[]>([]);
  
  // 加载用户地址
  useEffect(() => {
    const fetchAddresses = async () => {
      try {
        // 这里可以从后端加载用户地址
        // const addresses = await UserService.getUserAddresses(MOCK_USER_ID);
        // setAddresses(addresses);
        // setSelectedAddress(addresses.find(a => a.isDefault) || addresses[0] || null);
      } catch (error) {
        console.error('获取地址数据失败:', error);
      }
    };
    
    fetchAddresses();
  }, []);
  
  // 加载用户信用卡
  useEffect(() => {
    const fetchCreditCards = async () => {
      try {
        // 这里可以从后端加载用户信用卡
        // const cards = await PaymentService.getUserCreditCards(MOCK_USER_ID);
        // setCreditCards(cards);
        // setSelectedCard(cards.find(c => c.isDefault) || cards[0] || null);
      } catch (error) {
        console.error('获取信用卡数据失败:', error);
      }
    };
    
    if (paymentMethod === 'credit_card') {
      fetchCreditCards();
    }
  }, [paymentMethod]);
  
  // 加载分期方案
  useEffect(() => {
    const fetchInstallmentPlans = async () => {
      try {
        if (state?.actualAmount) {
          // 这里可以从后端加载分期方案
          // const plans = await PaymentService.getAvailableInstallmentPlans(state.actualAmount);
          // setInstallmentPlans(plans);
        }
      } catch (error) {
        console.error('获取分期方案数据失败:', error);
      }
    };
    
    if (paymentMethod === 'credit_card') {
      fetchInstallmentPlans();
    }
  }, [paymentMethod, state?.actualAmount]);
  
  // 处理地址选择
  const handleAddressSelect = (address: UserAddress) => {
    setSelectedAddress(address);
  };
  
  // 处理支付方式选择
  const handlePaymentMethodChange = (e: any) => {
    setPaymentMethod(e.target.value);
    if (e.target.value !== 'credit_card') {
      setInstallmentPlan(null);
    }
  };
  
  // 处理信用卡选择
  const handleCardSelect = (card: UserCreditCard) => {
    setSelectedCard(card);
  };
  
  // 处理分期方案选择
  const handleInstallmentChange = (planId: number | null) => {
    if (planId === null) {
      setInstallmentPlan(null);
    } else {
      const plan = mockInstallmentPlans.find(p => p.id === planId) || null;
      setInstallmentPlan(plan);
    }
  };
  
  // 处理提交订单
  const handleSubmitOrder = async () => {
    if (!state) {
      message.error('订单信息不完整');
      return;
    }
    
    if (!selectedAddress) {
      message.error('请选择收货地址');
      return;
    }
    
    if (paymentMethod === 'credit_card' && !selectedCard) {
      message.error('请选择信用卡');
      return;
    }
    
    try {
      setLoading(true);
      
      // 构建订单请求
      const orderRequest: CreateOrderRequest = {
        cartItemIds: state.cartItemIds,
        addressId: selectedAddress.id,
        paymentMethod,
        creditCardId: selectedCard?.id,
        installmentPlanId: installmentPlan?.id,
        couponId: state.coupon?.id,
        pointsUsed: state.pointsUsed,
        expectedAmount: state.actualAmount,
        remark
      };
      
      console.log('提交订单请求:', orderRequest);
      
      // 调用Redux异步action创建订单
      const resultAction = await dispatch(createOrderAsync(orderRequest));
      
      if (createOrderAsync.fulfilled.match(resultAction)) {
        console.log('订单创建成功，响应:', resultAction.payload);
        
        // 清空购物车
        dispatch(clearCart());
        
        // 创建路由状态
        const paymentState = { 
          orderId: resultAction.payload.orderNo || '123456789',
          amount: resultAction.payload.actualAmount || state.actualAmount,
          paymentMethod: paymentMethod,
          installmentPlanId: installmentPlan?.id
        };
        
        // 添加到本地订单列表（用于模拟）
        const newOrder: any = {
          id: resultAction.payload.orderNo,
          userId: MOCK_USER_ID,
          items: state.selectedItems,
          totalAmount: state.actualAmount,
          status: 'PENDING_PAYMENT', // 明确设置状态为待付款
          createdAt: new Date().toISOString(),
          addressId: selectedAddress.id,
          address: selectedAddress,
          paymentMethod: paymentMethod
        };
        
        // 添加到Redux订单列表
        dispatch(addLocalOrder(newOrder));
        
        console.log('准备跳转到支付页面，状态:', paymentState);
        
        // 跳转到支付页面
        navigate('/payment', { state: paymentState });
        
        message.success('订单提交成功！');
      } else {
        throw new Error('创建订单失败');
      }
    } catch (error) {
      console.error('提交订单失败:', error);
      message.error('提交订单失败，请稍后重试');
      
      // 模拟订单创建成功，使用模拟数据跳转到支付页面
      const mockPaymentState = { 
        orderId: 'ORD' + Date.now(),
        amount: state.actualAmount,
        paymentMethod: paymentMethod,
        installmentPlanId: installmentPlan?.id
      };
      
      // 添加到本地订单列表（用于模拟）
      const mockOrder: any = {
        id: mockPaymentState.orderId,
        userId: MOCK_USER_ID,
        items: state.selectedItems,
        totalAmount: state.actualAmount,
        status: 'PENDING_PAYMENT', // 明确设置状态为待付款
        createdAt: new Date().toISOString(),
        addressId: selectedAddress.id,
        address: selectedAddress,
        paymentMethod: paymentMethod
      };
      
      // 添加到Redux订单列表
      dispatch(addLocalOrder(mockOrder));
      
      console.log('使用模拟数据跳转到支付页面，状态:', mockPaymentState);
      navigate('/payment', { state: mockPaymentState });
    } finally {
      setLoading(false);
    }
  };
  
  // 渲染地址列表
  const renderAddressList = () => {
    return (
      <div>
        {mockAddresses.map(address => (
          <Card
            key={address.id}
            size="small"
            style={{ 
              marginBottom: 10, 
              cursor: 'pointer',
              border: selectedAddress?.id === address.id ? '2px solid #1890ff' : '1px solid #d9d9d9'
            }}
            onClick={() => handleAddressSelect(address)}
          >
            <Row justify="space-between" align="middle">
              <Col>
                <div>
                  <Text strong>{address.receiverName}</Text>
                  <Text style={{ marginLeft: 8 }}>{address.phone}</Text>
                  {address.isDefault && (
                    <Text type="secondary" style={{ marginLeft: 8 }}>默认</Text>
                  )}
                </div>
                <div style={{ marginTop: 4 }}>
                  <Text type="secondary">
                    {address.province} {address.city} {address.district} {address.detailAddress}
                  </Text>
                </div>
              </Col>
              <Col>
                <Space>
                  <Button 
                    type="text" 
                    icon={<EditOutlined />} 
                    onClick={(e) => {
                      e.stopPropagation();
                      // 这里应该打开编辑地址的模态框
                      message.info('编辑地址功能待实现');
                    }}
                  />
                  <Button 
                    type="text" 
                    danger 
                    icon={<DeleteOutlined />} 
                    onClick={(e) => {
                      e.stopPropagation();
                      // 这里应该删除地址
                      message.info('删除地址功能待实现');
                    }}
                  />
                </Space>
              </Col>
            </Row>
          </Card>
        ))}
        <Button 
          type="dashed" 
          block 
          icon={<PlusOutlined />}
          onClick={() => setAddressModalVisible(true)}
        >
          添加新地址
        </Button>
      </div>
    );
  };
  
  // 渲染信用卡列表
  const renderCreditCardList = () => {
    if (paymentMethod !== 'credit_card') {
      return null;
    }
    
    return (
      <div style={{ marginTop: 16 }}>
        <Title level={5}>选择信用卡</Title>
        {mockCreditCards.map(card => (
          <Card
            key={card.id}
            size="small"
            style={{ 
              marginBottom: 10, 
              cursor: 'pointer',
              border: selectedCard?.id === card.id ? '2px solid #1890ff' : '1px solid #d9d9d9'
            }}
            onClick={() => handleCardSelect(card)}
          >
            <Row justify="space-between" align="middle">
              <Col>
                <div>
                  <CreditCardOutlined style={{ marginRight: 8 }} />
                  <Text strong>{card.cardNumber}</Text>
                </div>
                <div style={{ marginTop: 4 }}>
                  <Text type="secondary">
                    {card.cardholderName} | 有效期: {card.expiryDate}
                  </Text>
                </div>
              </Col>
              <Col>
                <Space>
                  <Button 
                    type="text" 
                    icon={<EditOutlined />} 
                    onClick={(e) => {
                      e.stopPropagation();
                      // 这里应该打开编辑信用卡的模态框
                      message.info('编辑信用卡功能待实现');
                    }}
                  />
                </Space>
              </Col>
            </Row>
          </Card>
        ))}
        <Button 
          type="dashed" 
          block 
          icon={<PlusOutlined />}
          onClick={() => setCardModalVisible(true)}
        >
          添加新信用卡
        </Button>
        
        {/* 分期选择 */}
        {selectedCard && mockInstallmentPlans.length > 0 && (
          <div style={{ marginTop: 16 }}>
            <Title level={5}>分期付款</Title>
            <Select
              style={{ width: '100%' }}
              placeholder="请选择分期方案"
              value={installmentPlan?.id || null}
              onChange={handleInstallmentChange}
              allowClear
            >
              {mockInstallmentPlans.map(plan => (
                <Option key={plan.id} value={plan.id}>
                  {plan.planName} ({plan.installmentCount || 0}期)
                  {plan.interestRate > 0 ? ` - 年利率${plan.interestRate * 100}%` : ' - 免息'}
                </Option>
              ))}
            </Select>
            {installmentPlan && (
              <div style={{ marginTop: 8 }}>
                {installmentPlan.interestRate > 0 ? (
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    <Text type="secondary">
                      订单金额: ¥{state.actualAmount.toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      分期期数: {installmentPlan.installmentCount || 0}期
                    </Text>
                    <Text type="secondary">
                      年化利率: {(installmentPlan.interestRate * 100).toFixed(2)}%
                    </Text>
                    {/* 简单计算，实际应该使用更复杂的公式 */}
                    <Text type="secondary">
                      总利息: ¥{(state.actualAmount * installmentPlan.interestRate * ((installmentPlan.installmentCount || 0) / 12)).toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      总还款金额: ¥{(state.actualAmount * (1 + installmentPlan.interestRate * ((installmentPlan.installmentCount || 0) / 12))).toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      每期还款: ¥{(state.actualAmount * (1 + installmentPlan.interestRate * ((installmentPlan.installmentCount || 0) / 12)) / (installmentPlan.installmentCount || 1)).toFixed(2)}
                    </Text>
                  </Space>
                ) : (
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    <Text type="secondary">
                      订单金额: ¥{state.actualAmount.toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      分期期数: {installmentPlan.installmentCount || 0}期
                    </Text>
                    <Text type="secondary">
                      每期还款: ¥{(state.actualAmount / (installmentPlan.installmentCount || 1)).toFixed(2)}
                    </Text>
                    <Text type="secondary" strong style={{ color: '#52c41a' }}>
                      免息特惠: 总手续费¥0.00
                    </Text>
                  </Space>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    );
  };
  
  // 渲染商品列表
  const renderProductList = () => {
    if (!state || !state.selectedItems) {
      return null;
    }
    
    const columns = [
      {
        title: '商品信息',
        dataIndex: 'product',
        key: 'product',
        render: (product: any) => (
          <Space>
            <Image
              width={50}
              height={50}
              src={product.imageUrl || 'https://via.placeholder.com/50x50?text=暂无图片'}
              alt={product.name}
              style={{ objectFit: 'cover' }}
            />
            <Text>{product.name}</Text>
          </Space>
        ),
      },
      {
        title: '单价',
        dataIndex: 'product',
        key: 'price',
        width: 100,
        render: (product: any) => (
          <Text>¥{product.price}</Text>
        ),
      },
      {
        title: '数量',
        dataIndex: 'quantity',
        key: 'quantity',
        width: 80,
        render: (quantity: number) => (
          <Text>{quantity}</Text>
        ),
      },
      {
        title: '小计',
        key: 'subtotal',
        width: 100,
        render: (record: CartItem) => (
          <Text>¥{(record.product.price * record.quantity).toFixed(2)}</Text>
        ),
      },
    ];
    
    return (
      <Table
        columns={columns}
        dataSource={state.selectedItems}
        rowKey={(record) => record.product.id}
        pagination={false}
        size="small"
      />
    );
  };
  
  if (!state || !state.selectedItems) {
    return null;
  }
  
  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>确认订单</Title>
      
      {/* 收货地址 */}
      <Card title="收货地址" style={{ marginBottom: 20 }}>
        {renderAddressList()}
      </Card>
      
      {/* 商品信息 */}
      <Card title="商品信息" style={{ marginBottom: 20 }}>
        {renderProductList()}
      </Card>
      
      {/* 支付方式 */}
      <Card title="支付方式" style={{ marginBottom: 20 }}>
        <Radio.Group value={paymentMethod} onChange={handlePaymentMethodChange}>
          <Space direction="vertical" style={{ width: '100%' }}>
            {/* 模拟支付方式列表 */}
            <Radio value="credit_card">
              <Space>
                <CreditCardOutlined />
                信用卡支付
              </Space>
            </Radio>
            <Radio value="alipay">
              <Space>
                <AlipayCircleOutlined style={{ color: '#1677FF' }} />
                支付宝支付
              </Space>
            </Radio>
            <Radio value="wechat">
              <Space>
                <WechatOutlined style={{ color: '#07C160' }} />
                微信支付
              </Space>
            </Radio>
            <Radio value="bank_transfer">
              <Space>
                <BankOutlined />
                银行转账
              </Space>
            </Radio>
            <Radio value="wallet">
              <Space>
                <WalletOutlined />
                钱包支付
              </Space>
            </Radio>
          </Space>
        </Radio.Group>
        
        {paymentMethod === 'credit_card' && renderCreditCardList()}
      </Card>
      
      {/* 订单备注 */}
      <Card title="订单备注" style={{ marginBottom: 20 }}>
        <TextArea
          rows={3}
          placeholder="请输入订单备注信息"
          value={remark}
          onChange={(e) => setRemark(e.target.value)}
          maxLength={200}
        />
      </Card>
      
      {/* 订单金额 */}
      <Card style={{ marginBottom: 20 }}>
        <Row justify="end">
          <Col>
            <Space direction="vertical" align="end" style={{ width: '100%' }}>
              <Text>商品总价: ¥{state.selectedItems.reduce((total, item) => 
                total + item.product.price * item.quantity, 0).toFixed(2)}</Text>
              
              {state.discountAmount > 0 && (
                <Text>优惠金额: -¥{state.discountAmount.toFixed(2)}</Text>
              )}
              
              {state.pointsUsed > 0 && (
                <Text>积分抵扣: {state.pointsUsed} 积分</Text>
              )}
              
              <Text strong style={{ fontSize: 16 }}>
                应付金额: <span style={{ color: '#ff4d4f' }}>¥{state.actualAmount.toFixed(2)}</span>
              </Text>
            </Space>
          </Col>
        </Row>
      </Card>
      
      {/* 提交订单 */}
      <Row justify="end">
        <Col>
          <Button type="primary" size="large" onClick={handleSubmitOrder}>
            提交订单
          </Button>
        </Col>
      </Row>
      
      {/* 添加地址模态框 */}
      <Modal
        title="添加收货地址"
        open={addressModalVisible}
        onCancel={() => setAddressModalVisible(false)}
        onOk={() => {
          form.validateFields()
            .then(values => {
              console.log('新增地址:', values);
              message.success('地址添加成功');
              setAddressModalVisible(false);
              form.resetFields();
            })
            .catch(info => {
              console.log('验证失败:', info);
            });
        }}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="receiverName"
            label="收货人"
            rules={[{ required: true, message: '请输入收货人姓名' }]}
          >
            <Input placeholder="请输入收货人姓名" />
          </Form.Item>
          
          <Form.Item
            name="phone"
            label="手机号码"
            rules={[{ required: true, message: '请输入手机号码' }]}
          >
            <Input placeholder="请输入手机号码" />
          </Form.Item>
          
          <Form.Item
            name="province"
            label="省份"
            rules={[{ required: true, message: '请选择省份' }]}
          >
            <Select placeholder="请选择省份">
              <Option value="广东省">广东省</Option>
              <Option value="北京市">北京市</Option>
              <Option value="上海市">上海市</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            name="city"
            label="城市"
            rules={[{ required: true, message: '请选择城市' }]}
          >
            <Select placeholder="请选择城市">
              <Option value="深圳市">深圳市</Option>
              <Option value="广州市">广州市</Option>
              <Option value="东莞市">东莞市</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            name="district"
            label="区/县"
            rules={[{ required: true, message: '请选择区/县' }]}
          >
            <Select placeholder="请选择区/县">
              <Option value="南山区">南山区</Option>
              <Option value="福田区">福田区</Option>
              <Option value="罗湖区">罗湖区</Option>
            </Select>
          </Form.Item>
          
          <Form.Item
            name="detailAddress"
            label="详细地址"
            rules={[{ required: true, message: '请输入详细地址' }]}
          >
            <TextArea rows={2} placeholder="请输入详细地址" />
          </Form.Item>
          
          <Form.Item name="isDefault" valuePropName="checked">
            <Radio>设为默认地址</Radio>
          </Form.Item>
        </Form>
      </Modal>
      
      {/* 添加信用卡模态框 */}
      <Modal
        title="添加信用卡"
        open={cardModalVisible}
        onCancel={() => setCardModalVisible(false)}
        onOk={() => {
          form.validateFields()
            .then(values => {
              console.log('新增信用卡:', values);
              message.success('信用卡添加成功');
              setCardModalVisible(false);
              form.resetFields();
            })
            .catch(info => {
              console.log('验证失败:', info);
            });
        }}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="cardNumber"
            label="卡号"
            rules={[{ required: true, message: '请输入信用卡卡号' }]}
          >
            <Input placeholder="请输入信用卡卡号" />
          </Form.Item>
          
          <Form.Item
            name="cardholderName"
            label="持卡人姓名"
            rules={[{ required: true, message: '请输入持卡人姓名' }]}
          >
            <Input placeholder="请输入持卡人姓名" />
          </Form.Item>
          
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="expiryMonth"
                label="有效期(月)"
                rules={[{ required: true, message: '请选择月份' }]}
              >
                <Select placeholder="月">
                  {Array.from({ length: 12 }, (_, i) => (
                    <Option key={i + 1} value={i + 1}>
                      {String(i + 1).padStart(2, '0')}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="expiryYear"
                label="有效期(年)"
                rules={[{ required: true, message: '请选择年份' }]}
              >
                <Select placeholder="年">
                  {Array.from({ length: 10 }, (_, i) => {
                    const year = new Date().getFullYear() + i;
                    return (
                      <Option key={year} value={year}>
                        {year}
                      </Option>
                    );
                  })}
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Form.Item
            name="cvv"
            label="安全码"
            rules={[{ required: true, message: '请输入安全码' }]}
          >
            <Input placeholder="请输入卡背面的3位安全码" maxLength={3} />
          </Form.Item>
          
          <Form.Item name="isDefault" valuePropName="checked">
            <Radio>设为默认卡</Radio>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default OrderPage; 