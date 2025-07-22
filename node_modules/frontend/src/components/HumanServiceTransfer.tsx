import React, { useState, useEffect } from 'react';
import { Modal, Button, Progress, Spin, Result, Typography, Avatar, List, Badge, Alert, Card, Tag } from 'antd';
import { 
  CustomerServiceOutlined, 
  CloseOutlined, 
  LoadingOutlined, 
  CheckCircleOutlined, 
  UserOutlined,
  InfoCircleOutlined,
  ClockCircleOutlined,
  TeamOutlined
} from '@ant-design/icons';
import axios from 'axios';
import './HumanServiceTransfer.css';

const { Title, Paragraph, Text } = Typography;

interface HumanServiceTransferProps {
  visible: boolean;
  userId: string;
  sessionId: string;
  onCancel: () => void;
  onComplete: () => void;
}

interface QueueInfo {
  position: number;
  estimatedWaitTime: number;
  totalWaiting: number;
}

enum TransferStatus {
  WAITING = 'WAITING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  ERROR = 'ERROR'
}

const HumanServiceTransfer: React.FC<HumanServiceTransferProps> = ({
  visible,
  userId,
  sessionId,
  onCancel,
  onComplete
}) => {
  const [status, setStatus] = useState<TransferStatus>(TransferStatus.WAITING);
  const [queueInfo, setQueueInfo] = useState<QueueInfo | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [staffName, setStaffName] = useState<string>('');
  const [staffAvatar, setStaffAvatar] = useState<string>('');
  const [staffInfo, setStaffInfo] = useState<{department: string, specialties: string[]}>({
    department: '',
    specialties: []
  });
  const [loading, setLoading] = useState<boolean>(true);
  const [tipIndex, setTipIndex] = useState<number>(0);
  const [progressPercent, setProgressPercent] = useState<number>(0);

  // 等待小贴士
  const waitingTips = [
    "您可以在等待期间浏览我们的商品推荐",
    "转接人工客服期间，您的会话记录将被保留",
    "我们的客服人员经过专业培训，将为您提供最优质的服务",
    "您可以通过评价功能帮助我们改进服务质量",
    "如果您的问题紧急，可以尝试拨打客服热线"
  ];

  // 请求人工客服
  useEffect(() => {
    if (visible) {
      requestHumanService();
      
      // 定时获取队列信息
      const queueTimer = setInterval(() => {
        getQueueInfo();
      }, 5000);
      
      // 定时获取会话状态
      const statusTimer = setInterval(() => {
        getSessionStatus();
      }, 3000);
      
      // 定时切换小贴士
      const tipTimer = setInterval(() => {
        setTipIndex(prev => (prev + 1) % waitingTips.length);
      }, 8000);
      
      // 模拟进度条
      const progressTimer = setInterval(() => {
        setProgressPercent(prev => {
          if (prev >= 90) return prev;
          return prev + 1;
        });
      }, 1000);
      
      return () => {
        clearInterval(queueTimer);
        clearInterval(statusTimer);
        clearInterval(tipTimer);
        clearInterval(progressTimer);
      };
    } else {
      // 重置状态
      setStatus(TransferStatus.WAITING);
      setQueueInfo(null);
      setError(null);
      setStaffName('');
      setStaffAvatar('');
      setProgressPercent(0);
    }
  }, [visible]);

  // 请求人工客服
  const requestHumanService = async () => {
    try {
      setLoading(true);
      const response = await axios.post('/api/human-service/request', null, {
        params: {
          userId,
          sessionId,
          reason: '用户请求人工客服'
        }
      });
      
      if (response.data.code === 200) {
        setStatus(response.data.data.status as TransferStatus);
        if (response.data.data.staffId) {
          setStaffName(response.data.data.staffId);
          setStatus(TransferStatus.IN_PROGRESS);
          
          // 模拟获取客服头像和信息
          setStaffAvatar(`https://randomuser.me/api/portraits/men/${Math.floor(Math.random() * 100)}.jpg`);
          setStaffInfo({
            department: '客户服务部',
            specialties: ['订单问题', '退换货', '商品咨询']
          });
        }
      } else {
        setError(response.data.message || '请求人工客服失败');
        setStatus(TransferStatus.ERROR);
      }
    } catch (error) {
      console.error('请求人工客服出错:', error);
      setError('网络错误，请稍后再试');
      setStatus(TransferStatus.ERROR);
    } finally {
      setLoading(false);
    }
  };

  // 获取队列信息
  const getQueueInfo = async () => {
    if (status !== TransferStatus.WAITING) return;
    
    try {
      const response = await axios.get('/api/human-service/queue-info', {
        params: { sessionId }
      });
      
      if (response.data.code === 200) {
        setQueueInfo(response.data.data);
      }
    } catch (error) {
      console.error('获取队列信息失败:', error);
      // 模拟队列信息
      setQueueInfo({
        position: Math.floor(Math.random() * 5) + 1,
        estimatedWaitTime: Math.floor(Math.random() * 10) + 1,
        totalWaiting: Math.floor(Math.random() * 10) + 5
      });
    }
  };

  // 获取会话状态
  const getSessionStatus = async () => {
    try {
      const response = await axios.get('/api/human-service/status', {
        params: { userId, sessionId }
      });
      
      if (response.data.code === 200) {
        const newStatus = response.data.data.status as TransferStatus;
        setStatus(newStatus);
        
        if (newStatus === TransferStatus.IN_PROGRESS && response.data.data.staffId) {
          setStaffName(response.data.data.staffId);
          
          // 模拟获取客服头像
          setStaffAvatar(`https://randomuser.me/api/portraits/men/${Math.floor(Math.random() * 100)}.jpg`);
          setStaffInfo({
            department: '客户服务部',
            specialties: ['订单问题', '退换货', '商品咨询']
          });
          
          // 设置进度为100%
          setProgressPercent(100);
        }
        
        if (newStatus === TransferStatus.COMPLETED) {
          setTimeout(() => {
            onComplete();
          }, 1500);
        }
      }
    } catch (error) {
      console.error('获取会话状态失败:', error);
    }
  };

  // 取消请求
  const handleCancel = () => {
    // 发送取消请求
    axios.post('/api/human-service/cancel', null, {
      params: { sessionId }
    }).catch(error => {
      console.error('取消请求失败:', error);
    });
    
    setStatus(TransferStatus.CANCELLED);
    onCancel();
  };

  // 渲染等待界面
  const renderWaiting = () => (
    <div className="transfer-waiting">
      <div className="waiting-header">
        <Title level={4}>正在为您转接人工客服</Title>
        <Paragraph>
          <Text type="secondary">
            我们正在安排专业客服为您服务，请稍候...
          </Text>
        </Paragraph>
      </div>
      
      <div className="waiting-progress">
        <Progress 
          percent={progressPercent} 
          status="active" 
          strokeColor={{
            '0%': '#108ee9',
            '100%': '#87d068',
          }}
        />
      </div>
      
      {queueInfo && (
        <div className="queue-info">
          <Card size="small" className="queue-card">
            <div className="queue-stats">
              <div className="queue-stat-item">
                <Badge count={queueInfo.position} overflowCount={99} />
                <div className="stat-label">当前位置</div>
              </div>
              <div className="queue-stat-item">
                <Badge 
                  count={queueInfo.estimatedWaitTime} 
                  style={{ backgroundColor: '#52c41a' }} 
                  overflowCount={99} 
                />
                <div className="stat-label">预计等待(分钟)</div>
              </div>
              <div className="queue-stat-item">
                <Badge 
                  count={queueInfo.totalWaiting} 
                  style={{ backgroundColor: '#1890ff' }} 
                  overflowCount={99} 
                />
                <div className="stat-label">排队人数</div>
              </div>
            </div>
          </Card>
        </div>
      )}
      
      <div className="waiting-tip">
        <Alert
          icon={<InfoCircleOutlined />}
          message={waitingTips[tipIndex]}
          type="info"
          showIcon
        />
      </div>
      
      <div className="waiting-actions">
        <Button 
          type="primary" 
          danger 
          onClick={handleCancel}
          icon={<CloseOutlined />}
        >
          取消转接
        </Button>
      </div>
    </div>
  );

  // 渲染已连接界面
  const renderConnected = () => (
    <div className="transfer-connected">
      <Result
        icon={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
        title="已成功接入人工客服"
        subTitle="客服人员将为您提供专业的服务"
        extra={
          <Button type="primary" onClick={onComplete}>
            开始对话
          </Button>
        }
      />
      
      <div className="staff-info">
        <Card title="客服信息" size="small">
          <div className="staff-profile">
            <Avatar 
              size={64} 
              src={staffAvatar} 
              icon={<UserOutlined />} 
            />
            <div className="staff-details">
              <div className="staff-name">{staffName}</div>
              <div className="staff-department">{staffInfo.department}</div>
              <div className="staff-specialties">
                {staffInfo.specialties.map((specialty, index) => (
                  <Tag key={index} color="blue">{specialty}</Tag>
                ))}
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );

  // 渲染错误界面
  const renderError = () => (
    <div className="transfer-error">
      <Result
        status="error"
        title="接入失败"
        subTitle={error || '暂时无法接入人工客服，请稍后再试'}
        extra={[
          <Button key="retry" type="primary" onClick={requestHumanService}>
            重试
          </Button>,
          <Button key="cancel" onClick={onCancel}>
            取消
          </Button>
        ]}
      />
    </div>
  );

  // 根据状态渲染不同界面
  const renderContent = () => {
    if (loading) {
      return (
        <div className="transfer-loading">
          <Spin 
            indicator={<LoadingOutlined style={{ fontSize: 36 }} spin />} 
            tip="正在连接人工客服..." 
          />
        </div>
      );
    }
    
    switch (status) {
      case TransferStatus.IN_PROGRESS:
        return renderConnected();
      case TransferStatus.ERROR:
        return renderError();
      case TransferStatus.WAITING:
      default:
        return renderWaiting();
    }
  };

  return (
    <Modal
      title={
        <div className="transfer-title">
          <CustomerServiceOutlined className="title-icon" /> 
          <span>人工客服</span>
          {status === TransferStatus.WAITING && queueInfo && (
            <Badge 
              count={queueInfo.position} 
              style={{ backgroundColor: '#1890ff' }} 
              className="title-badge"
            />
          )}
        </div>
      }
      open={visible}
      onCancel={handleCancel}
      footer={null}
      width={400}
      className="human-service-transfer-modal"
    >
      {renderContent()}
    </Modal>
  );
};

export default HumanServiceTransfer; 