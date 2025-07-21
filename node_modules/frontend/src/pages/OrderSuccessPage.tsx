import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Result, Button, Typography, Card, Divider } from 'antd';
import { CheckCircleOutlined, HomeOutlined, FileTextOutlined } from '@ant-design/icons';

const { Title, Text, Paragraph } = Typography;

interface OrderSuccessState {
  orderId: string;
  amount: number;
}

const OrderSuccessPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  // 从路由状态中获取数据
  const state = location.state as OrderSuccessState;
  
  // 如果没有传递状态，重定向到首页
  if (!state || !state.orderId) {
    navigate('/');
    return null;
  }
  
  return (
    <div style={{ padding: '40px 20px', maxWidth: 800, margin: '0 auto' }}>
      <Result
        icon={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
        title="订单提交成功！"
        subTitle={`订单号: ${state.orderId}`}
        extra={[
          <Button 
            type="primary" 
            key="home" 
            icon={<HomeOutlined />}
            onClick={() => navigate('/')}
          >
            返回首页
          </Button>,
          <Button 
            key="orders" 
            icon={<FileTextOutlined />}
            onClick={() => navigate('/orders')}
          >
            查看订单
          </Button>,
        ]}
      />
      
      <Card style={{ marginTop: 24 }}>
        <Title level={4}>订单信息</Title>
        <Divider />
        
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
          <Text>订单编号:</Text>
          <Text strong>{state.orderId}</Text>
        </div>
        
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
          <Text>订单金额:</Text>
          <Text strong style={{ color: '#ff4d4f', fontSize: 16 }}>
            ¥{state.amount.toFixed(2)}
          </Text>
        </div>
        
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
          <Text>支付状态:</Text>
          <Text strong style={{ color: '#52c41a' }}>已支付</Text>
        </div>
        
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
          <Text>下单时间:</Text>
          <Text>{new Date().toLocaleString('zh-CN')}</Text>
        </div>
        
        <Divider />
        
        <Paragraph style={{ textAlign: 'center', color: '#888' }}>
          感谢您的购买！我们将尽快为您发货。
        </Paragraph>
      </Card>
    </div>
  );
};

export default OrderSuccessPage; 