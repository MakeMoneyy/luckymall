import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Table, 
  Tabs, 
  Button, 
  Space, 
  Typography, 
  Tag, 
  Modal, 
  Descriptions,
  List,
  Image,
  Empty,
  Steps,
  Badge,
  Row,
  Col,
  Divider,
  Spin,
  message,
  Alert
} from 'antd';
import { 
  EyeOutlined, 
  FileTextOutlined, 
  ShoppingCartOutlined,
  CarOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  WalletOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { RootState, AppDispatch } from '../store';
import { fetchUserOrdersAsync, cancelOrderAsync, confirmReceiveAsync, addLocalOrder } from '../store/slices/orderSlice';
import { CartItem, Order } from '../types';

const { Title, Text } = Typography;
const { TabPane } = Tabs;
const { Step } = Steps;

const OrderHistoryPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  
  // 从Redux获取订单数据
  const { orders, loading, error } = useSelector((state: RootState) => state.order);
  
  // 加载订单数据
  useEffect(() => {
    console.log('OrderHistoryPage: 加载订单数据');
    
    // 从localStorage获取订单数据
    const localOrders = localStorage.getItem('orders');
    if (localOrders) {
      try {
        const parsedOrders = JSON.parse(localOrders);
        console.log('从localStorage加载订单数据:', parsedOrders);
        
        // 手动更新Redux状态
        if (Array.isArray(parsedOrders) && parsedOrders.length > 0) {
          // 确保所有订单都有状态
          const ordersWithStatus = parsedOrders.map((order: any) => {
            if (!order.status) {
              return { ...order, status: 'PENDING_PAYMENT' };
            }
            return order;
          });
          
          // 更新Redux状态
          dispatch({ type: 'order/fetchUserOrders/fulfilled', payload: ordersWithStatus });
        }
      } catch (error) {
        console.error('解析localStorage订单数据失败:', error);
      }
    } else {
      // 如果localStorage中没有数据，则从API获取
      dispatch(fetchUserOrdersAsync());
    }
    
    // 组件卸载时清除缓存
    return () => {
      // 清除任何可能存在的缓存
    };
  }, [dispatch]);
  
  // 输出订单数据，用于调试
  useEffect(() => {
    console.log('OrderHistoryPage: 订单数据更新', orders);
  }, [orders]);
  
  // 根据状态筛选订单
  const getOrdersByStatus = (status?: string) => {
    if (!status || status === 'all') {
      return orders;
    }
    return orders.filter(order => order.status === status);
  };
  
  // 订单状态映射
  const getStatusTag = (status: string) => {
    console.log('获取订单状态标签:', status);
    
    const statusMap: Record<string, { color: string; text: string }> = {
      PENDING_PAYMENT: { color: 'orange', text: '待付款' },
      PAID: { color: 'blue', text: '已付款' },
      PROCESSING: { color: 'purple', text: '处理中' },
      SHIPPED: { color: 'cyan', text: '已发货' },
      COMPLETED: { color: 'green', text: '已完成' },
      CANCELLED: { color: 'red', text: '已取消' },
      // 添加默认状态
      undefined: { color: 'default', text: '未知' },
      null: { color: 'default', text: '未知' }
    };
    
    // 如果状态不存在，默认显示"未知"
    const config = statusMap[status] || { color: 'default', text: '未知' };
    console.log('状态配置:', config);
    return <Tag color={config.color}>{config.text}</Tag>;
  };
  
  // 获取订单当前步骤
  const getOrderStep = (status: string) => {
    const stepMap: Record<string, number> = {
      PENDING_PAYMENT: 0,
      PAID: 1,
      PROCESSING: 1,
      SHIPPED: 2,
      COMPLETED: 3,
      CANCELLED: -1
    };
    
    return stepMap[status] !== undefined ? stepMap[status] : 0;
  };
  
  // 显示订单详情
  const showOrderDetail = (order: Order) => {
    console.log('显示订单详情:', order);
    setSelectedOrder(order);
    setDetailModalVisible(true);
  };
  
  // 处理订单操作
  const handleOrderAction = async (order: Order, action: string) => {
    console.log('处理订单操作:', action, order);
    
    if (action === 'pay') {
      // 跳转到支付页面
      navigate('/payment', { 
        state: { 
          orderId: order.id, 
          amount: order.totalAmount,
          paymentMethod: order.paymentMethod
        } 
      });
    } else if (action === 'cancel') {
      Modal.confirm({
        title: '确认取消订单',
        content: `您确定要取消订单 ${order.id} 吗？`,
        onOk: () => {
          // 直接使用原生JavaScript操作localStorage
          try {
            const ordersStr = localStorage.getItem('orders');
            if (ordersStr) {
              const orders = JSON.parse(ordersStr);
              const orderIndex = orders.findIndex((o: any) => o.id === order.id);
              
              if (orderIndex !== -1) {
                // 更新订单状态
                orders[orderIndex].status = 'CANCELLED';
                
                // 保存回localStorage
                localStorage.setItem('orders', JSON.stringify(orders));
                
                // 显示成功消息
                message.success('订单已取消');
                
                // 强制刷新整个页面
                window.location.reload();
              } else {
                message.error('未找到订单');
              }
            } else {
              message.error('未找到订单数据');
            }
          } catch (error) {
            console.error('取消订单失败:', error);
            message.error('取消订单失败');
          }
        }
      });
    } else if (action === 'confirm') {
      Modal.confirm({
        title: '确认收货',
        content: `您确定已收到订单 ${order.id} 的商品吗？`,
        onOk: () => {
          // 直接使用原生JavaScript操作localStorage
          try {
            const ordersStr = localStorage.getItem('orders');
            if (ordersStr) {
              const orders = JSON.parse(ordersStr);
              const orderIndex = orders.findIndex((o: any) => o.id === order.id);
              
              if (orderIndex !== -1) {
                // 更新订单状态
                orders[orderIndex].status = 'COMPLETED';
                
                // 保存回localStorage
                localStorage.setItem('orders', JSON.stringify(orders));
                
                // 显示成功消息
                message.success('已确认收货');
                
                // 强制刷新整个页面
                window.location.reload();
              } else {
                message.error('未找到订单');
              }
            } else {
              message.error('未找到订单数据');
            }
          } catch (error) {
            console.error('确认收货失败:', error);
            message.error('确认收货失败');
          }
        }
      });
    }
  };
  
  // 刷新订单列表
  const handleRefresh = () => {
    console.log('刷新订单列表');
    
    // 强制刷新整个页面
    window.location.reload();
  };
  
  // 渲染订单操作按钮
  const renderOrderActions = (order: Order) => {
    console.log('渲染订单操作按钮:', order);
    
    // 如果订单状态为空，默认为待付款
    const status = order.status || 'PENDING_PAYMENT';
    
    switch (status) {
      case 'PENDING_PAYMENT':
        return (
          <Space>
            <Button 
              type="primary" 
              size="small" 
              onClick={() => handleOrderAction(order, 'pay')}
            >
              立即付款
            </Button>
            <Button 
              danger
              size="small" 
              onClick={() => handleOrderAction(order, 'cancel')}
            >
              取消订单
            </Button>
          </Space>
        );
      case 'SHIPPED':
        return (
          <Button 
            type="primary" 
            size="small" 
            onClick={() => handleOrderAction(order, 'confirm')}
          >
            确认收货
          </Button>
        );
      case 'COMPLETED':
        return (
          <Button 
            size="small" 
            onClick={() => navigate(`/review?orderId=${order.id}`)}
          >
            评价商品
          </Button>
        );
      case 'CANCELLED':
        return (
          <Button 
            size="small" 
            disabled
          >
            已取消
          </Button>
        );
      case 'PAID':
      case 'PROCESSING':
        return (
          <Button 
            size="small" 
            disabled
          >
            处理中
          </Button>
        );
      default:
        console.log('未知订单状态:', status);
        return (
          <Button 
            size="small" 
            onClick={() => handleOrderAction(order, 'cancel')}
          >
            取消订单
          </Button>
        );
    }
  };
  
  // 表格列定义
  const columns = [
    {
      title: '订单信息',
      key: 'info',
      render: (order: Order) => {
        console.log('渲染订单信息:', order);
        return (
          <div style={{ display: 'flex' }}>
            <div style={{ marginRight: 16, minWidth: 80 }}>
              {order.items && order.items.length > 0 ? (
                <>
                  <Image
                    width={80}
                    height={80}
                    src={order.items[0].product?.imageUrl || 'https://via.placeholder.com/80x80?text=暂无图片'}
                    alt={order.items[0].product?.name || '商品图片'}
                    style={{ objectFit: 'cover' }}
                  />
                  {order.items.length > 1 && (
                    <div style={{ textAlign: 'center', marginTop: 4 }}>
                      <Text type="secondary">共{order.items.length}件</Text>
                    </div>
                  )}
                </>
              ) : (
                <Image
                  width={80}
                  height={80}
                  src="https://via.placeholder.com/80x80?text=暂无图片"
                  alt="暂无商品"
                  style={{ objectFit: 'cover' }}
                />
              )}
            </div>
            <div>
              {order.items && order.items.length > 0 ? (
                order.items.slice(0, 2).map((item, index) => (
                  <div key={index} style={{ marginBottom: 8 }}>
                    <Text ellipsis style={{ maxWidth: 300, display: 'block' }}>
                      {item.product?.name || '未知商品'}
                    </Text>
                    <div>
                      <Text type="secondary" style={{ fontSize: 12 }}>
                        {item.product?.categoryName || '未知分类'} | 数量: {item.quantity}
                      </Text>
                    </div>
                  </div>
                ))
              ) : (
                <Text>订单号: {order.id}</Text>
              )}
              {order.items && order.items.length > 2 && (
                <Text type="secondary">等 {order.items.length} 件商品</Text>
              )}
            </div>
          </div>
        );
      },
    },
    {
      title: '订单金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 120,
      render: (amount: number) => (
        <Text strong style={{ color: '#ff4d4f' }}>
          ¥{amount ? amount.toFixed(2) : '0.00'}
        </Text>
      ),
    },
    {
      title: '订单状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '下单时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 160,
      render: (date: string) => date ? new Date(date).toLocaleString('zh-CN') : '未知',
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (record: Order) => (
        <Space>
          {renderOrderActions(record)}
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => showOrderDetail(record)}
          >
            查看详情
          </Button>
        </Space>
      ),
    },
  ];
  
  return (
    <div style={{ padding: '20px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={2}>我的订单</Title>
        <Space>
          <Button 
            icon={<ReloadOutlined />} 
            onClick={handleRefresh}
            loading={loading}
          >
            刷新
          </Button>
          <Button type="primary" danger onClick={() => window.open('/order-manager.html', '_blank')}>
            订单管理器
          </Button>
        </Space>
      </div>
      
      {error && (
        <div style={{ marginBottom: 16 }}>
          <Alert type="error" message={error} />
        </div>
      )}
      
      <Card bodyStyle={{ padding: 0 }}>
        <Spin spinning={loading}>
          <Tabs defaultActiveKey="all" size="large" style={{ padding: '0 16px' }}>
            <TabPane tab="全部订单" key="all">
              <Table
                columns={columns}
                dataSource={getOrdersByStatus('all')}
                rowKey="id"
                pagination={{ pageSize: 10 }}
                locale={{ emptyText: <Empty description="暂无订单" /> }}
              />
            </TabPane>
            <TabPane tab={
              <Badge count={getOrdersByStatus('PENDING_PAYMENT').length} offset={[10, 0]}>
                待付款
              </Badge>
            } key="PENDING_PAYMENT">
              <Table
                columns={columns}
                dataSource={getOrdersByStatus('PENDING_PAYMENT')}
                rowKey="id"
                pagination={{ pageSize: 10 }}
                locale={{ emptyText: <Empty description="暂无待付款订单" /> }}
              />
            </TabPane>
            <TabPane tab="待发货" key="PAID">
              <Table
                columns={columns}
                dataSource={getOrdersByStatus('PAID')}
                rowKey="id"
                pagination={{ pageSize: 10 }}
                locale={{ emptyText: <Empty description="暂无待发货订单" /> }}
              />
            </TabPane>
            <TabPane tab="待收货" key="SHIPPED">
              <Table
                columns={columns}
                dataSource={getOrdersByStatus('SHIPPED')}
                rowKey="id"
                pagination={{ pageSize: 10 }}
                locale={{ emptyText: <Empty description="暂无待收货订单" /> }}
              />
            </TabPane>
            <TabPane tab="已完成" key="COMPLETED">
              <Table
                columns={columns}
                dataSource={getOrdersByStatus('COMPLETED')}
                rowKey="id"
                pagination={{ pageSize: 10 }}
                locale={{ emptyText: <Empty description="暂无已完成订单" /> }}
              />
            </TabPane>
          </Tabs>
          
          {orders.length === 0 && !loading && (
            <Empty
              image={<FileTextOutlined style={{ fontSize: 64, color: '#ccc' }} />}
              description="暂无订单"
              style={{ padding: '60px 0' }}
            >
              <Button type="primary" onClick={() => navigate('/')}>
                去购物
              </Button>
            </Empty>
          )}
        </Spin>
      </Card>
      
      {/* 订单详情模态框 */}
      <Modal
        title="订单详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={800}
      >
        {selectedOrder && (
          <div>
            {/* 订单状态进度条 */}
            <div style={{ margin: '20px 0 40px' }}>
              <Steps current={getOrderStep(selectedOrder.status)} status={selectedOrder.status === 'CANCELLED' ? 'error' : 'process'}>
                <Step title="提交订单" icon={<ShoppingCartOutlined />} />
                <Step title="商家发货" icon={<CarOutlined />} />
                <Step title="交易完成" icon={<CheckCircleOutlined />} />
              </Steps>
              {selectedOrder.status === 'CANCELLED' && (
                <div style={{ textAlign: 'center', marginTop: 16 }}>
                  <Text type="danger" strong>
                    <CloseCircleOutlined style={{ marginRight: 8 }} />
                    订单已取消
                  </Text>
                </div>
              )}
            </div>
            
            <Row gutter={24}>
              <Col span={16}>
                {/* 订单信息 */}
                <Card title="订单信息" style={{ marginBottom: 16 }}>
                  <Descriptions column={1} bordered size="small">
                    <Descriptions.Item label="订单编号">{selectedOrder.id}</Descriptions.Item>
                    <Descriptions.Item label="下单时间">
                      {new Date(selectedOrder.createdAt).toLocaleString('zh-CN')}
                    </Descriptions.Item>
                    <Descriptions.Item label="支付方式">
                      {selectedOrder.paymentMethod === 'credit_card' && (
                        <span><WalletOutlined style={{ marginRight: 8 }} />信用卡支付</span>
                      )}
                      {selectedOrder.paymentMethod === 'alipay' && (
                        <span><WalletOutlined style={{ marginRight: 8 }} />支付宝支付</span>
                      )}
                      {selectedOrder.paymentMethod === 'wechat' && (
                        <span><WalletOutlined style={{ marginRight: 8 }} />微信支付</span>
                      )}
                      {selectedOrder.paymentMethod === 'bank_transfer' && (
                        <span><WalletOutlined style={{ marginRight: 8 }} />银行转账</span>
                      )}
                      {selectedOrder.paymentMethod === 'balance' && (
                        <span><WalletOutlined style={{ marginRight: 8 }} />余额支付</span>
                      )}
                    </Descriptions.Item>
                  </Descriptions>
                </Card>
                
                {/* 商品清单 */}
                <Card title="商品清单" style={{ marginBottom: 16 }}>
                  <List
                    dataSource={selectedOrder.items || []}
                    renderItem={(item: CartItem) => (
                      <List.Item>
                        <List.Item.Meta
                          avatar={
                            <Image
                              width={60}
                              height={60}
                              src={item.product.imageUrl || 'https://via.placeholder.com/60x60?text=暂无图片'}
                              alt={item.product.name}
                              style={{ objectFit: 'cover' }}
                            />
                          }
                          title={item.product.name}
                          description={
                            <Space direction="vertical" size="small">
                              <Text type="secondary">{item.product.categoryName}</Text>
                              <Text>数量: {item.quantity}</Text>
                            </Space>
                          }
                        />
                        <div>
                          <Text strong style={{ color: '#ff4d4f' }}>
                            ¥{item.product.price}
                          </Text>
                        </div>
                      </List.Item>
                    )}
                  />
                </Card>
              </Col>
              
              <Col span={8}>
                {/* 金额信息 */}
                <Card title="金额信息">
                  <div style={{ padding: '0 16px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                      <Text>商品总价:</Text>
                      <Text>¥{selectedOrder.totalAmount.toFixed(2)}</Text>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                      <Text>运费:</Text>
                      <Text>¥0.00</Text>
                    </div>
                    <Divider style={{ margin: '12px 0' }} />
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                      <Text strong>实付款:</Text>
                      <Text strong style={{ color: '#ff4d4f', fontSize: 16 }}>
                        ¥{selectedOrder.totalAmount.toFixed(2)}
                      </Text>
                    </div>
                  </div>
                </Card>
                
                {/* 订单操作 */}
                {renderOrderActions(selectedOrder) && (
                  <Card title="订单操作" style={{ marginTop: 16 }}>
                    <div style={{ textAlign: 'center', padding: '16px 0' }}>
                      {renderOrderActions(selectedOrder)}
                    </div>
                  </Card>
                )}
              </Col>
            </Row>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default OrderHistoryPage; 