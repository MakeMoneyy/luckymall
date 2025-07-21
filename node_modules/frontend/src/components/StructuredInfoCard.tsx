import React from 'react';
import { Card, Typography, Descriptions, Tag, Button, Divider, Timeline } from 'antd';
import {
  ShoppingOutlined,
  CarOutlined,
  CreditCardOutlined,
  GiftOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  SyncOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';
import './StructuredInfoCard.css';

const { Title, Text } = Typography;

export enum CardType {
  ORDER = 'order',
  LOGISTICS = 'logistics',
  PAYMENT = 'payment',
  POINTS = 'points',
  GENERIC = 'generic'
}

export enum OrderStatus {
  PENDING = 'pending',
  PAID = 'paid',
  SHIPPED = 'shipped',
  DELIVERED = 'delivered',
  CANCELLED = 'cancelled'
}

export enum LogisticsStatus {
  PENDING = 'pending',
  PROCESSING = 'processing',
  SHIPPED = 'shipped',
  IN_TRANSIT = 'in_transit',
  DELIVERED = 'delivered',
  EXCEPTION = 'exception'
}

interface BaseCardProps {
  title: string;
  type: CardType;
  actions?: React.ReactNode[];
}

interface OrderCardProps extends BaseCardProps {
  type: CardType.ORDER;
  orderNumber: string;
  orderDate: string;
  status: OrderStatus;
  items: Array<{
    name: string;
    quantity: number;
    price: number;
  }>;
  totalAmount: number;
}

interface LogisticsCardProps extends BaseCardProps {
  type: CardType.LOGISTICS;
  trackingNumber: string;
  carrier: string;
  status: LogisticsStatus;
  estimatedDelivery?: string;
  events: Array<{
    time: string;
    location: string;
    description: string;
  }>;
}

interface PaymentCardProps extends BaseCardProps {
  type: CardType.PAYMENT;
  paymentMethod: string;
  amount: number;
  date: string;
  status: string;
  installmentPlan?: {
    totalMonths: number;
    monthlyPayment: number;
    remainingMonths: number;
  };
}

interface PointsCardProps extends BaseCardProps {
  type: CardType.POINTS;
  balance: number;
  expiring?: {
    amount: number;
    date: string;
  };
  recentTransactions: Array<{
    date: string;
    description: string;
    amount: number;
    isEarned: boolean;
  }>;
}

interface GenericCardProps extends BaseCardProps {
  type: CardType.GENERIC;
  content: React.ReactNode;
}

type StructuredInfoCardProps =
  | OrderCardProps
  | LogisticsCardProps
  | PaymentCardProps
  | PointsCardProps
  | GenericCardProps;

const StructuredInfoCard: React.FC<StructuredInfoCardProps> = (props) => {
  const { title, type, actions = [] } = props;

  // 渲染订单卡片
  const renderOrderCard = (props: OrderCardProps) => {
    const { orderNumber, orderDate, status, items, totalAmount } = props;
    
    const getStatusTag = (status: OrderStatus) => {
      switch (status) {
        case OrderStatus.PENDING:
          return <Tag icon={<ClockCircleOutlined />} color="warning">待付款</Tag>;
        case OrderStatus.PAID:
          return <Tag icon={<CheckCircleOutlined />} color="processing">已付款</Tag>;
        case OrderStatus.SHIPPED:
          return <Tag icon={<CarOutlined />} color="processing">已发货</Tag>;
        case OrderStatus.DELIVERED:
          return <Tag icon={<CheckCircleOutlined />} color="success">已送达</Tag>;
        case OrderStatus.CANCELLED:
          return <Tag icon={<ExclamationCircleOutlined />} color="error">已取消</Tag>;
        default:
          return <Tag>未知状态</Tag>;
      }
    };

    return (
      <div className="order-card-content">
        <Descriptions column={2} size="small">
          <Descriptions.Item label="订单号">{orderNumber}</Descriptions.Item>
          <Descriptions.Item label="订单日期">{orderDate}</Descriptions.Item>
          <Descriptions.Item label="订单状态">{getStatusTag(status)}</Descriptions.Item>
          <Descriptions.Item label="订单金额">¥{totalAmount.toFixed(2)}</Descriptions.Item>
        </Descriptions>
        
        <Divider orientation="left">商品明细</Divider>
        
        <div className="order-items">
          {items.map((item, index) => (
            <div key={index} className="order-item">
              <div className="item-name">{item.name}</div>
              <div className="item-quantity">x{item.quantity}</div>
              <div className="item-price">¥{item.price.toFixed(2)}</div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  // 渲染物流卡片
  const renderLogisticsCard = (props: LogisticsCardProps) => {
    const { trackingNumber, carrier, status, estimatedDelivery, events } = props;
    
    const getStatusTag = (status: LogisticsStatus) => {
      switch (status) {
        case LogisticsStatus.PENDING:
          return <Tag icon={<ClockCircleOutlined />} color="default">待处理</Tag>;
        case LogisticsStatus.PROCESSING:
          return <Tag icon={<SyncOutlined spin />} color="processing">处理中</Tag>;
        case LogisticsStatus.SHIPPED:
          return <Tag icon={<CarOutlined />} color="processing">已发货</Tag>;
        case LogisticsStatus.IN_TRANSIT:
          return <Tag icon={<SyncOutlined spin />} color="processing">运输中</Tag>;
        case LogisticsStatus.DELIVERED:
          return <Tag icon={<CheckCircleOutlined />} color="success">已送达</Tag>;
        case LogisticsStatus.EXCEPTION:
          return <Tag icon={<ExclamationCircleOutlined />} color="error">异常</Tag>;
        default:
          return <Tag>未知状态</Tag>;
      }
    };

    return (
      <div className="logistics-card-content">
        <Descriptions column={2} size="small">
          <Descriptions.Item label="运单号">{trackingNumber}</Descriptions.Item>
          <Descriptions.Item label="物流公司">{carrier}</Descriptions.Item>
          <Descriptions.Item label="物流状态">{getStatusTag(status)}</Descriptions.Item>
          {estimatedDelivery && (
            <Descriptions.Item label="预计送达">{estimatedDelivery}</Descriptions.Item>
          )}
        </Descriptions>
        
        <Divider orientation="left">物流轨迹</Divider>
        
        <Timeline mode="left">
          {events.map((event, index) => (
            <Timeline.Item 
              key={index} 
              color={index === 0 ? 'green' : 'blue'}
              label={event.time}
            >
              <div className="event-location">{event.location}</div>
              <div className="event-description">{event.description}</div>
            </Timeline.Item>
          ))}
        </Timeline>
      </div>
    );
  };

  // 渲染支付卡片
  const renderPaymentCard = (props: PaymentCardProps) => {
    const { paymentMethod, amount, date, status, installmentPlan } = props;
    
    return (
      <div className="payment-card-content">
        <Descriptions column={2} size="small">
          <Descriptions.Item label="支付方式">{paymentMethod}</Descriptions.Item>
          <Descriptions.Item label="支付金额">¥{amount.toFixed(2)}</Descriptions.Item>
          <Descriptions.Item label="支付日期">{date}</Descriptions.Item>
          <Descriptions.Item label="支付状态">
            <Tag color={status === '成功' ? 'success' : 'error'}>{status}</Tag>
          </Descriptions.Item>
        </Descriptions>
        
        {installmentPlan && (
          <>
            <Divider orientation="left">分期信息</Divider>
            <Descriptions column={2} size="small">
              <Descriptions.Item label="分期方案">{installmentPlan.totalMonths}期</Descriptions.Item>
              <Descriptions.Item label="每月还款">¥{installmentPlan.monthlyPayment.toFixed(2)}</Descriptions.Item>
              <Descriptions.Item label="剩余期数">{installmentPlan.remainingMonths}期</Descriptions.Item>
            </Descriptions>
          </>
        )}
      </div>
    );
  };

  // 渲染积分卡片
  const renderPointsCard = (props: PointsCardProps) => {
    const { balance, expiring, recentTransactions } = props;
    
    return (
      <div className="points-card-content">
        <div className="points-balance">
          <Title level={3}>{balance}</Title>
          <Text type="secondary">当前积分</Text>
        </div>
        
        {expiring && (
          <div className="points-expiring">
            <Tag color="warning" icon={<ClockCircleOutlined />}>
              {expiring.amount} 积分将于 {expiring.date} 过期
            </Tag>
          </div>
        )}
        
        <Divider orientation="left">近期积分变动</Divider>
        
        <div className="points-transactions">
          {recentTransactions.map((transaction, index) => (
            <div key={index} className="transaction-item">
              <div className="transaction-date">{transaction.date}</div>
              <div className="transaction-description">{transaction.description}</div>
              <div className={`transaction-amount ${transaction.isEarned ? 'earned' : 'spent'}`}>
                {transaction.isEarned ? '+' : '-'}{transaction.amount}
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  // 根据类型渲染不同的卡片内容
  const renderCardContent = () => {
    switch (props.type) {
      case CardType.ORDER:
        return renderOrderCard(props as OrderCardProps);
      case CardType.LOGISTICS:
        return renderLogisticsCard(props as LogisticsCardProps);
      case CardType.PAYMENT:
        return renderPaymentCard(props as PaymentCardProps);
      case CardType.POINTS:
        return renderPointsCard(props as PointsCardProps);
      case CardType.GENERIC:
        return (props as GenericCardProps).content;
      default:
        return null;
    }
  };

  // 根据类型获取图标
  const getCardIcon = () => {
    switch (type) {
      case CardType.ORDER:
        return <ShoppingOutlined />;
      case CardType.LOGISTICS:
        return <CarOutlined />;
      case CardType.PAYMENT:
        return <CreditCardOutlined />;
      case CardType.POINTS:
        return <GiftOutlined />;
      default:
        return null;
    }
  };

  return (
    <Card
      className={`structured-info-card ${type}-card`}
      title={
        <div className="card-title">
          {getCardIcon()} {title}
        </div>
      }
      actions={actions}
    >
      {renderCardContent()}
    </Card>
  );
};

export default StructuredInfoCard; 