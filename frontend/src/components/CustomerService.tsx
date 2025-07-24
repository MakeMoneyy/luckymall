import React, { useState, useEffect, useRef } from 'react';
import { Button, Input, Spin, Avatar, Badge, Drawer, message, Tooltip, Divider } from 'antd';
import { SendOutlined, CustomerServiceOutlined, CloseOutlined, UserOutlined } from '@ant-design/icons';
import { v4 as uuidv4 } from 'uuid';
import '../styles/CustomerService.css';
import { sendChatMessage, ChatResponse } from '../services/chatService';
import HumanServiceTransfer from './HumanServiceTransfer';
import NaturalLanguageInput from './NaturalLanguageInput';
import EmotionDisplay, { EmotionData } from './EmotionDisplay';
import StructuredInfoCard, { CardType, OrderStatus, LogisticsStatus } from './StructuredInfoCard';

interface Message {
  id: string;
  content: string;
  sender: 'user' | 'bot' | 'human';
  timestamp: Date;
  structuredData?: any;
  emotion?: EmotionData;
}

const CustomerService: React.FC = () => {
  const [visible, setVisible] = useState<boolean>(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [sessionId, setSessionId] = useState<string>('');
  const [humanServiceVisible, setHumanServiceVisible] = useState<boolean>(false);
  const [isHumanServiceMode, setIsHumanServiceMode] = useState<boolean>(false);
  const [showEmotionDetails, setShowEmotionDetails] = useState<boolean>(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 初始化会话ID
  useEffect(() => {
    const storedSessionId = localStorage.getItem('chatSessionId');
    if (storedSessionId) {
      setSessionId(storedSessionId);
    } else {
      const newSessionId = uuidv4();
      setSessionId(newSessionId);
      localStorage.setItem('chatSessionId', newSessionId);
    }
  }, []);

  // 自动滚动到最新消息
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // 显示客服窗口
  const showDrawer = () => {
    setVisible(true);
    // 如果是首次打开，显示欢迎消息
    if (messages.length === 0) {
      setMessages([
        {
          id: uuidv4(),
          content: '您好！我是招财商城的智能客服助手，有什么可以帮您的吗？',
          sender: 'bot',
          timestamp: new Date()
        }
      ]);
    }
  };

  // 关闭客服窗口
  const closeDrawer = () => {
    setVisible(false);
  };

  // 请求人工客服
  const requestHumanService = () => {
    setHumanServiceVisible(true);
  };

  // 取消人工客服请求
  const cancelHumanService = () => {
    setHumanServiceVisible(false);
  };

  // 完成人工客服转接
  const completeHumanService = () => {
    setHumanServiceVisible(false);
    setIsHumanServiceMode(true);
    
    // 添加系统消息
    const systemMessage: Message = {
      id: uuidv4(),
      content: '您已成功接入人工客服，请开始对话。',
      sender: 'human',
      timestamp: new Date()
    };
    
    setMessages(prev => [...prev, systemMessage]);
  };

  // 处理自然语言输入
  const handleNaturalLanguageSearch = (query: string) => {
    handleSendMessage(query);
  };

  // 发送消息
  const handleSendMessage = async (messageText: string) => {
    if (!messageText.trim()) return;

    // 添加用户消息到列表
    const userMessage: Message = {
      id: uuidv4(),
      content: messageText,
      sender: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setLoading(true);

    // 如果是人工客服模式，直接模拟人工回复
    if (isHumanServiceMode) {
      setTimeout(() => {
        const humanResponse: Message = {
          id: uuidv4(),
          content: '您好，我是人工客服小王，很高兴为您服务。请问有什么可以帮助您的？',
          sender: 'human',
          timestamp: new Date()
        };
        setMessages(prev => [...prev, humanResponse]);
        setLoading(false);
      }, 1000);
      return;
    }

    try {
      // 调用后端API
      const response = await sendChatMessage({
        userId: 'web-user',
        sessionId: sessionId,
        message: messageText,
        context: {}
      });

      // 检查是否需要转人工
      if (response.result && response.result.includes('已将您的对话转接给人工客服')) {
        // 显示转人工界面
        setHumanServiceVisible(true);
      }

      // 解析结构化数据（模拟，实际应该从后端返回）
      const structuredData = parseStructuredData(messageText, response);
      
      // 解析情感数据（模拟，实际应该从后端返回）
      const emotionData = parseEmotionData(messageText);

      // 添加机器人回复
      const botMessage: Message = {
        id: uuidv4(),
        content: response.result || '抱歉，我暂时无法回答您的问题。',
        sender: 'bot',
        timestamp: new Date(),
        structuredData,
        emotion: emotionData
      };

      setMessages(prev => [...prev, botMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      message.error('发送消息失败，请稍后再试');
      
      // 添加错误提示
      const errorMessage: Message = {
        id: uuidv4(),
        content: '抱歉，系统暂时无法回应，请稍后再试。',
        sender: 'bot',
        timestamp: new Date()
      };

      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  // 处理按键事件
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSendMessage(inputValue);
    }
  };

  // 解析结构化数据（模拟）
  const parseStructuredData = (message: string, response: ChatResponse): any | undefined => {
    // 这里应该是根据后端返回的数据进行解析
    // 这里只是模拟一些示例数据
    
    if (message.toLowerCase().includes('订单')) {
      return {
        type: CardType.ORDER,
        title: '订单信息',
        orderNumber: 'ORD12345678',
        orderDate: '2023-05-20',
        status: OrderStatus.SHIPPED,
        items: [
          { name: 'iPhone 13 Pro Max', quantity: 1, price: 8999 },
          { name: '手机保护壳', quantity: 2, price: 99 }
        ],
        totalAmount: 9197
      };
    }
    
    if (message.toLowerCase().includes('物流')) {
      return {
        type: CardType.LOGISTICS,
        title: '物流信息',
        trackingNumber: 'SF1234567890',
        carrier: '顺丰速运',
        status: LogisticsStatus.IN_TRANSIT,
        estimatedDelivery: '2023-05-25',
        events: [
          { time: '2023-05-22 10:30', location: '上海分拣中心', description: '已发出' },
          { time: '2023-05-21 18:45', location: '广州转运中心', description: '运输中' },
          { time: '2023-05-21 09:20', location: '广州仓库', description: '已出库' }
        ]
      };
    }
    
    if (message.toLowerCase().includes('积分')) {
      return {
        type: CardType.POINTS,
        title: '积分信息',
        balance: 8500,
        expiring: {
          amount: 2000,
          date: '2023-06-30'
        },
        recentTransactions: [
          { date: '2023-05-15', description: '购物消费', amount: 500, isEarned: true },
          { date: '2023-05-10', description: '积分兑换', amount: 1000, isEarned: false },
          { date: '2023-05-01', description: '生日奖励', amount: 200, isEarned: true }
        ]
      };
    }
    
    return undefined;
  };

  // 解析情感数据（模拟）
  const parseEmotionData = (message: string): EmotionData | undefined => {
    // 这里应该是根据后端返回的数据进行解析
    // 这里只是模拟一些示例数据
    
    const lowerMessage = message.toLowerCase();
    
    // 检测正面情绪
    if (lowerMessage.includes('谢谢') || lowerMessage.includes('感谢') || 
        lowerMessage.includes('好的') || lowerMessage.includes('满意')) {
      return {
        type: 'POSITIVE',
        intensity: 4,
        keywords: ['感谢', '满意'],
        trend: 'IMPROVING'
      };
    }
    
    // 检测负面情绪
    if (lowerMessage.includes('不满') || lowerMessage.includes('差') || 
        lowerMessage.includes('退款') || lowerMessage.includes('投诉')) {
      return {
        type: 'NEGATIVE',
        intensity: 2,
        keywords: ['不满', '投诉'],
        trend: 'DETERIORATING'
      };
    }
    
    // 默认中性
    return {
      type: 'NEUTRAL',
      intensity: 3,
      trend: 'STABLE'
    };
  };

  // 渲染消息内容
  const renderMessageContent = (msg: Message) => {
    // 如果有结构化数据，渲染结构化卡片
    if (msg.structuredData) {
      return (
        <div className="message-structured-data">
          <div className="message-text">{msg.content}</div>
          <StructuredInfoCard {...msg.structuredData} />
        </div>
      );
    }
    
    // 否则渲染普通文本
    return <div className="message-text">{msg.content}</div>;
  };

  // 渲染情感指示器
  const renderEmotionIndicator = (msg: Message) => {
    if (msg.sender !== 'user' || !msg.emotion) return null;
    
    return (
      <div className="message-emotion">
        <EmotionDisplay 
          emotion={msg.emotion} 
          showDetails={showEmotionDetails}
          size="small"
        />
        {!showEmotionDetails && (
          <Button 
            type="link" 
            size="small" 
            onClick={() => setShowEmotionDetails(true)}
          >
            查看详情
          </Button>
        )}
      </div>
    );
  };

  return (
    <>
      {/* 悬浮按钮 */}
      <Badge dot>
        <Button
          type="primary"
          shape="circle"
          icon={<CustomerServiceOutlined />}
          size="large"
          className="customer-service-button"
          onClick={showDrawer}
        />
      </Badge>

      {/* 聊天抽屉 */}
      <Drawer
        title={
          <div className="customer-service-header">
            <span>{isHumanServiceMode ? "人工客服" : "智能客服"}</span>
            {!isHumanServiceMode && (
              <Tooltip title="转人工客服">
                <Button 
                  type="link" 
                  icon={<UserOutlined />} 
                  onClick={requestHumanService}
                  size="small"
                >
                  转人工
                </Button>
              </Tooltip>
            )}
          </div>
        }
        placement="right"
        onClose={closeDrawer}
        open={visible}
        width={360}
        closeIcon={<CloseOutlined />}
        className="customer-service-drawer"
        footer={
          <div className="customer-service-footer">
            {/* 使用自然语言输入组件 */}
            <NaturalLanguageInput 
              onSearch={handleNaturalLanguageSearch}
              loading={loading}
              placeholder="您可以直接询问如'查询我最近的订单'或'我想退货'"
            />
          </div>
        }
      >
        <div className="customer-service-messages">
          {messages.map(msg => (
            <div
              key={msg.id}
              className={`message-bubble ${
                msg.sender === 'user' 
                  ? 'user-message' 
                  : msg.sender === 'human' 
                    ? 'human-message' 
                    : 'bot-message'
              }`}
            >
              {msg.sender !== 'user' && (
                <Avatar 
                  className={msg.sender === 'human' ? 'human-avatar' : 'bot-avatar'} 
                  icon={msg.sender === 'human' ? <UserOutlined /> : <CustomerServiceOutlined />} 
                />
              )}
              <div className="message-content">
                {renderMessageContent(msg)}
                <div className="message-time">
                  {msg.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </div>
              </div>
              {renderEmotionIndicator(msg)}
            </div>
          ))}
          {loading && (
            <div className="message-bubble bot-message">
              <Avatar 
                className={isHumanServiceMode ? 'human-avatar' : 'bot-avatar'} 
                icon={isHumanServiceMode ? <UserOutlined /> : <CustomerServiceOutlined />} 
              />
              <div className="message-content loading-content">
                <Spin size="small" />
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>
      </Drawer>

      {/* 人工客服转接界面 */}
      <HumanServiceTransfer
        visible={humanServiceVisible}
        userId="web-user"
        sessionId={sessionId}
        onCancel={cancelHumanService}
        onComplete={completeHumanService}
      />
    </>
  );
};

export default CustomerService; 