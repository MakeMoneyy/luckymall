import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Typography, 
  Radio, 
  Space, 
  Button, 
  Steps,
  message,
  Result,
  Spin,
  Divider,
  Modal,
  Image,
  Select,
  Row,
  Col
} from 'antd';
import { 
  CreditCardOutlined, 
  BankOutlined, 
  WalletOutlined,
  CheckCircleOutlined,
  AlipayCircleOutlined,
  WechatOutlined
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation, useNavigate } from 'react-router-dom';
import { RootState, AppDispatch } from '../store';
import { PaymentService, PaymentRequest } from '../services/paymentService';
import { OrderService } from '../services/orderService';
import { addLocalOrder } from '../store/slices/orderSlice';
import { fetchUserOrdersAsync } from '../store/slices/orderSlice';

const { Title, Text, Paragraph } = Typography;
const { Step } = Steps;

interface PaymentPageState {
  orderId: string;
  amount: number;
  paymentMethod?: string;
  installmentPlanId?: number;
}

// 模拟用户ID
const MOCK_USER_ID = 1;

// 模拟二维码图片
const MOCK_ALIPAY_QR_CODE = 'https://via.placeholder.com/300x300?text=支付宝支付';
const MOCK_WECHAT_QR_CODE = 'https://via.placeholder.com/300x300?text=微信支付';

// 模拟分期方案数据
const mockInstallmentPlans = [
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

const PaymentPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  
  // 从路由状态中获取数据
  const state = location.state as PaymentPageState;
  
  console.log('PaymentPage接收到的状态:', state);
  
  // 如果没有传递状态，重定向到首页
  useEffect(() => {
    if (!state) {
      console.error('没有接收到支付信息，重定向到首页');
      navigate('/');
      message.error('支付信息不完整');
      return;
    }
    
    if (!state.orderId) {
      console.error('没有接收到订单号，重定向到首页');
      navigate('/');
      message.error('订单号不存在');
      return;
    }
    
    console.log('支付页面初始化完成，订单号:', state.orderId, '金额:', state.amount);
  }, [state, navigate]);
  
  // 支付状态
  const [paymentMethod, setPaymentMethod] = useState<string>(
    state?.paymentMethod || 'credit_card'
  );
  const [paymentStep, setPaymentStep] = useState<number>(0);
  const [processing, setProcessing] = useState<boolean>(false);
  const [selectedCardId, setSelectedCardId] = useState<number | undefined>(undefined);
  const [selectedInstallmentPlanId, setSelectedInstallmentPlanId] = useState<number | undefined>(
    state?.installmentPlanId
  );
  const [installmentPlan, setInstallmentPlan] = useState<any>(
    state?.installmentPlanId ? mockInstallmentPlans.find(p => p.id === state.installmentPlanId) : null
  );
  
  // 二维码模态框状态
  const [qrCodeVisible, setQrCodeVisible] = useState<boolean>(false);
  const [qrCodeImage, setQrCodeImage] = useState<string>('');
  const [qrCodeTitle, setQrCodeTitle] = useState<string>('');
  
  // 处理支付方式选择
  const handlePaymentMethodChange = (e: any) => {
    setPaymentMethod(e.target.value);
  };
  
  // 处理分期方案选择
  const handleInstallmentChange = (planId: number | null) => {
    if (planId === null) {
      setSelectedInstallmentPlanId(undefined);
      setInstallmentPlan(null);
    } else {
      setSelectedInstallmentPlanId(planId);
      const plan = mockInstallmentPlans.find(p => p.id === planId) || null;
      setInstallmentPlan(plan);
    }
  };
  
  // 处理支付提交
  const handlePaymentSubmit = async () => {
    if (!state) return;
    
    // 对于支付宝和微信支付，显示二维码
    if (paymentMethod === 'alipay' || paymentMethod === 'wechat') {
      setQrCodeTitle(paymentMethod === 'alipay' ? '支付宝支付' : '微信支付');
      setQrCodeImage(paymentMethod === 'alipay' ? MOCK_ALIPAY_QR_CODE : MOCK_WECHAT_QR_CODE);
      setQrCodeVisible(true);
      return;
    }
    
    try {
      setProcessing(true);
      
      // 构建支付请求
      const paymentRequest: PaymentRequest = {
        orderId: parseInt(state.orderId),
        paymentMethod,
        amount: state.amount,
        creditCardId: selectedCardId,
        installmentPlanId: selectedInstallmentPlanId
      };
      
      console.log('支付请求:', paymentRequest);
      
      // 调用支付API
      // 实际项目中应该调用PaymentService.payOrder
      // const response = await PaymentService.payOrder(MOCK_USER_ID, paymentRequest);
      
      // 模拟支付处理
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // 调用订单支付API
      // await OrderService.payOrder(parseInt(state.orderId), MOCK_USER_ID);
      
      // 更新支付状态
      setPaymentStep(1);
      message.success('支付成功！');
      
      // 添加到本地订单列表（用于模拟）
      const newOrder: any = {
        id: state.orderId,
        status: 'PAID', // 更新状态为已支付
        paymentMethod: paymentMethod,
        paymentTime: new Date().toISOString()
      };
      
      console.log('支付成功，更新订单状态:', newOrder);
      
      // 添加到Redux订单列表
      dispatch(addLocalOrder(newOrder));
      
      // 刷新订单列表
      dispatch(fetchUserOrdersAsync());
    } catch (error) {
      console.error('支付失败:', error);
      message.error('支付失败，请稍后重试');
    } finally {
      setProcessing(false);
    }
  };
  
  // 处理二维码支付完成
  const handleQrCodePaymentComplete = async () => {
    setQrCodeVisible(false);
    
    try {
      setProcessing(true);
      
      // 模拟支付处理
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 更新支付状态
      setPaymentStep(1);
      message.success('支付成功！');
      
      // 添加到本地订单列表（用于模拟）
      const newOrder: any = {
        id: state.orderId,
        status: 'PAID', // 更新状态为已支付
        paymentMethod: paymentMethod,
        paymentTime: new Date().toISOString()
      };
      
      console.log('二维码支付成功，更新订单状态:', newOrder);
      
      // 添加到Redux订单列表
      dispatch(addLocalOrder(newOrder));
      
      // 刷新订单列表
      dispatch(fetchUserOrdersAsync());
    } catch (error) {
      console.error('支付失败:', error);
      message.error('支付失败，请稍后重试');
    } finally {
      setProcessing(false);
    }
  };
  
  // 处理返回订单列表
  const handleBackToOrders = () => {
    navigate('/orders');
  };
  
  // 处理继续购物
  const handleContinueShopping = () => {
    navigate('/');
  };
  
  // 渲染支付方式
  const renderPaymentMethods = () => {
    return (
      <div>
        <Title level={4}>支付方式</Title>
        <Radio.Group value={paymentMethod} onChange={handlePaymentMethodChange}>
          <Space direction="vertical" style={{ width: '100%' }}>
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
            <Radio value="balance">
              <Space>
                <WalletOutlined />
                余额支付
              </Space>
            </Radio>
          </Space>
        </Radio.Group>
        
        {/* 信用卡分期选项 */}
        {paymentMethod === 'credit_card' && (
          <div style={{ marginTop: 16 }}>
            <Row>
              <Col span={24}>
                <Title level={5}>分期付款</Title>
                <Select
                  style={{ width: '100%' }}
                  placeholder="请选择分期方案"
                  value={selectedInstallmentPlanId || null}
                  onChange={handleInstallmentChange}
                  allowClear
                >
                  {mockInstallmentPlans.map(plan => (
                    <Select.Option key={plan.id} value={plan.id}>
                      {plan.planName} ({plan.installmentCount || 0}期)
                      {plan.interestRate > 0 ? ` - 年利率${plan.interestRate * 100}%` : ' - 免息'}
                    </Select.Option>
                  ))}
                </Select>
              </Col>
            </Row>
            
            {installmentPlan && (
              <div style={{ marginTop: 8 }}>
                {installmentPlan.interestRate > 0 ? (
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    <Text type="secondary">
                      订单金额: ¥{state?.amount.toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      分期期数: {installmentPlan.installmentCount || 0}期
                    </Text>
                    <Text type="secondary">
                      年化利率: {(installmentPlan.interestRate * 100).toFixed(2)}%
                    </Text>
                    {/* 简单计算，实际应该使用更复杂的公式 */}
                    <Text type="secondary">
                      总利息: ¥{(state?.amount * installmentPlan.interestRate * ((installmentPlan.installmentCount || 0) / 12)).toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      总还款金额: ¥{(state?.amount * (1 + installmentPlan.interestRate * ((installmentPlan.installmentCount || 0) / 12))).toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      每期还款: ¥{(state?.amount * (1 + installmentPlan.interestRate * ((installmentPlan.installmentCount || 0) / 12)) / (installmentPlan.installmentCount || 1)).toFixed(2)}
                    </Text>
                  </Space>
                ) : (
                  <Space direction="vertical" size="small" style={{ width: '100%' }}>
                    <Text type="secondary">
                      订单金额: ¥{state?.amount.toFixed(2)}
                    </Text>
                    <Text type="secondary">
                      分期期数: {installmentPlan.installmentCount || 0}期
                    </Text>
                    <Text type="secondary">
                      每期还款: ¥{(state?.amount / (installmentPlan.installmentCount || 1)).toFixed(2)}
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
  
  if (!state) {
    return (
      <div style={{ padding: '20px', maxWidth: 800, margin: '0 auto' }}>
        <Card>
          <Result
            status="error"
            title="加载失败"
            subTitle="没有接收到支付信息，请返回重新下单"
            extra={[
              <Button type="primary" key="home" onClick={() => navigate('/')}>
                返回首页
              </Button>,
              <Button key="cart" onClick={() => navigate('/cart')}>
                查看购物车
              </Button>,
            ]}
          />
        </Card>
      </div>
    );
  }
  
  return (
    <div style={{ padding: '20px', maxWidth: 800, margin: '0 auto' }}>
      <Card>
        <Steps current={paymentStep} style={{ marginBottom: 40 }}>
          <Step title="选择支付方式" />
          <Step title="支付完成" />
        </Steps>
        
        {paymentStep === 0 && (
          <div>
            <div style={{ marginBottom: 24 }}>
              <Title level={4}>订单信息</Title>
              <div style={{ background: '#f5f5f5', padding: 16, borderRadius: 4 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                  <Text>订单编号:</Text>
                  <Text>{state.orderId}</Text>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Text>应付金额:</Text>
                  <Text strong style={{ color: '#ff4d4f', fontSize: 16 }}>
                    ¥{state.amount?.toFixed(2) || '0.00'}
                  </Text>
                </div>
              </div>
            </div>
            
            {renderPaymentMethods()}
            
            <div style={{ marginTop: 24, textAlign: 'center' }}>
              <Button
                type="primary"
                size="large"
                onClick={handlePaymentSubmit}
                loading={processing}
              >
                确认支付
              </Button>
            </div>
          </div>
        )}
        
        {paymentStep === 1 && (
          <Result
            status="success"
            title="支付成功！"
            subTitle={`订单号: ${state.orderId} 已支付完成，感谢您的购买！`}
            extra={[
              <Button type="primary" key="orders" onClick={handleBackToOrders}>
                查看订单
              </Button>,
              <Button key="buy" onClick={handleContinueShopping}>继续购物</Button>,
            ]}
          />
        )}
      </Card>
      
      {/* 二维码支付模态框 */}
      <Modal
        title={qrCodeTitle}
        open={qrCodeVisible}
        onCancel={() => setQrCodeVisible(false)}
        footer={[
          <Button key="cancel" onClick={() => setQrCodeVisible(false)}>
            取消
          </Button>,
          <Button key="complete" type="primary" onClick={handleQrCodePaymentComplete}>
            支付完成
          </Button>,
        ]}
      >
        <div style={{ textAlign: 'center' }}>
          <Paragraph>
            请使用{paymentMethod === 'alipay' ? '支付宝' : '微信'}扫描下方二维码进行支付
          </Paragraph>
          <div style={{ margin: '20px 0' }}>
            <Image
              src={qrCodeImage}
              alt={qrCodeTitle}
              width={200}
              height={200}
              style={{ margin: '0 auto' }}
            />
          </div>
          <div style={{ background: '#f5f5f5', padding: 16, borderRadius: 4, textAlign: 'left' }}>
            <div style={{ marginBottom: 8 }}>
              <Text>订单编号: {state?.orderId}</Text>
            </div>
            <div>
              <Text>应付金额: </Text>
              <Text strong style={{ color: '#ff4d4f' }}>
                ¥{state?.amount.toFixed(2)}
              </Text>
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default PaymentPage; 