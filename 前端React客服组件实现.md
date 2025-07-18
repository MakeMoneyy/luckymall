// ==================== React组件实现 ====================

// 1. 主要的客服聊天组件
// CustomerServiceChat.jsx
import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import './CustomerServiceChat.css';

const CustomerServiceChat = ({ 
  isOpen, 
  onClose, 
  productInfo, 
  userId = 1001,
  currentPage = window.location.pathname 
}) => {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId] = useState(() => 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9));
  const [cacheStats, setCacheStats] = useState(null);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  // 快捷回复选项
  const quickResponses = [
    "这个商品可以分期吗？",
    "用信用卡支付有什么优惠？",
    "我的积分可以抵扣多少？",
    "支付方式有哪些？",
    "信用卡有什么权益？"
  ];

  // 组件初始化
  useEffect(() => {
    if (isOpen && messages.length === 0) {
      // 发送欢迎消息
      const welcomeMessage = {
        type: 'bot',
        content: '您好！我是招商银行信用卡购物助手🏦\n\n有什么可以帮您的吗？使用信用卡支付还能享受专属优惠哦～',
        timestamp: new Date(),
        responseTime: 0
      };
      setMessages([welcomeMessage]);
    }
  }, [isOpen]);

  // 自动滚动到底部
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 获取缓存统计（开发调试用）
  useEffect(() => {
    if (isOpen) {
      fetchCacheStats();
    }
  }, [isOpen]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const fetchCacheStats = async () => {
    try {
      const response = await axios.get('/api/customer-service/cache-stats');
      setCacheStats(response.data);
    } catch (error) {
      console.error('获取缓存统计失败:', error);
    }
  };

  const sendMessage = async (message) => {
    if (!message.trim()) return;

    // 添加用户消息到聊天记录
    const userMessage = {
      type: 'user',
      content: message,
      timestamp: new Date()
    };
    
    setMessages(prev => [...prev, userMessage]);
    setInputMessage('');
    setIsLoading(true);

    try {
      const requestData = {
        userId: userId,
        sessionId: sessionId,
        message: message,
        context: {
          productId: productInfo?.id,
          productName: productInfo?.name,
          productPrice: productInfo?.price,
          currentPage: currentPage,
          cartItems: getCartItems() // 从localStorage或状态管理获取
        }
      };

      console.log('发送请求:', requestData);

      const response = await axios.post('/api/customer-service/chat', requestData);
      
      const botMessage = {
        type: 'bot',
        content: response.data.message,
        suggestions: response.data.suggestions,
        promotionInfo: response.data.promotionInfo,
        responseTime: response.data.responseTime,
        timestamp: new Date()
      };

      setMessages(prev => [...prev, botMessage]);
      
      // 更新缓存统计
      fetchCacheStats();

    } catch (error) {
      console.error('发送消息失败:', error);
      const errorMessage = {
        type: 'bot',
        content: '抱歉，客服暂时忙碌，请稍后再试或联系人工客服 📞\n\n您也可以继续浏览商品，使用信用卡支付享受更多优惠！',
        timestamp: new Date(),
        responseTime: 0
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleQuickResponse = (response) => {
    sendMessage(response);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage(inputMessage);
    }
  };

  const getCartItems = () => {
    // 模拟获取购物车商品
    try {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]');
      return cart.map(item => item.id);
    } catch {
      return [];
    }
  };

  const handlePromotionClick = (action) => {
    switch (action) {
      case 'learn_more':
        sendMessage('我想了解更多信用卡权益');
        break;
      case 'use_credit_card':
        // 跳转到支付页面并选择信用卡
        window.location.href = '/checkout?payment=credit_card';
        break;
      case 'check_installment':
        sendMessage('这个商品可以分几期？每期多少钱？');
        break;
      default:
        break;
    }
  };

  if (!isOpen) return null;

  return (
    <div className="customer-service-chat">
      {/* 聊天头部 */}
      <div className="chat-header">
        <div className="header-info">
          <h3>🏦 招商银行客服</h3>
          <span className="online-status">● 在线</span>
        </div>
        <div className="header-actions">
          {/* 缓存状态指示器（开发调试用） */}
          {cacheStats && (
            <div className="cache-indicator" title={`缓存命中率: ${cacheStats.hitRate.toFixed(1)}%`}>
              📊 {cacheStats.hitRate.toFixed(1)}%
            </div>
          )}
          <button onClick={onClose} className="close-btn">×</button>
        </div>
      </div>
      
      {/* 聊天消息区域 */}
      <div className="chat-messages">
        {messages.map((message, index) => (
          <div key={index} className={`message ${message.type}`}>
            <div className="message-content">
              <div className="message-text">
                {message.content.split('\n').map((line, i) => (
                  <React.Fragment key={i}>
                    {line}
                    {i < message.content.split('\n').length - 1 && <br />}
                  </React.Fragment>
                ))}
              </div>
              
              {/* 响应时间显示 */}
              {message.responseTime !== undefined && (
                <div className="response-time">
                  ⚡ {message.responseTime}ms
                  {message.responseTime < 100 && <span className="cache-hit"> (缓存)</span>}
                </div>
              )}
              
              {/* 信用卡推广信息 */}
              {message.promotionInfo && (
                <PromotionCard 
                  promotionInfo={message.promotionInfo}
                  onAction={handlePromotionClick}
                />
              )}
              
              {/* 建议回复按钮 */}
              {message.suggestions && message.suggestions.length > 0 && (
                <div className="suggestions">
                  <div className="suggestions-title">您可能想问：</div>
                  {message.suggestions.map((suggestion, idx) => (
                    <button 
                      key={idx} 
                      onClick={() => handleQuickResponse(suggestion)}
                      className="suggestion-btn"
                    >
                      {suggestion}
                    </button>
                  ))}
                </div>
              )}
            </div>
            
            <div className="message-meta">
              <span className="message-time">
                {message.timestamp.toLocaleTimeString()}
              </span>
              {message.type === 'bot' && (
                <span className="message-source">🤖</span>
              )}
            </div>
          </div>
        ))}
        
        {/* 加载指示器 */}
        {isLoading && (
          <div className="message bot">
            <div className="typing-indicator">
              <span></span><span></span><span></span>
            </div>
            <div className="typing-text">客服正在输入...</div>
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>
      
      {/* 快捷回复区域 */}
      <div className="quick-responses">
        <div className="quick-responses-title">💡 快捷咨询：</div>
        <div className="quick-responses-grid">
          {quickResponses.map((response, index) => (
            <button 
              key={index}
              onClick={() => handleQuickResponse(response)}
              className="quick-response-btn"
              disabled={isLoading}
            >
              {response}
            </button>
          ))}
        </div>
      </div>
      
      {/* 输入区域 */}
      <div className="chat-input">
        <div className="input-wrapper">
          <textarea
            ref={inputRef}
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="输入您的问题..."
            disabled={isLoading}
            rows={1}
            style={{ 
              minHeight: '40px',
              maxHeight: '120px',
              resize: 'none'
            }}
          />
          <button 
            onClick={() => sendMessage(inputMessage)}
            disabled={isLoading || !inputMessage.trim()}
            className="send-btn"
          >
            {isLoading ? '...' : '发送'}
          </button>
        </div>
        
        {/* 输入提示 */}
        <div className="input-hint">
          💡 提示：询问支付方式、积分使用、商品信息等
        </div>
      </div>
    </div>
  );
};

// 2. 信用卡推广卡片组件
const PromotionCard = ({ promotionInfo, onAction }) => {
  if (!promotionInfo) return null;

  return (
    <div className="promotion-card">
      <div className="promotion-header">
        <div className="promotion-title">{promotionInfo.title}</div>
        <div className="promotion-badge">专享</div>
      </div>
      
      <div className="promotion-benefits">
        {promotionInfo.pointsEarned && (
          <div className="benefit-item">
            <span className="benefit-icon">🎁</span>
            <span className="benefit-text">
              获得 {promotionInfo.pointsEarned} 积分
              <small>（价值 ¥{promotionInfo.pointsValue?.toFixed(1)}）</small>
            </span>
          </div>
        )}
        
        {promotionInfo.freeInterestDays && (
          <div className="benefit-item">
            <span className="benefit-icon">⏰</span>
            <span className="benefit-text">
              {promotionInfo.freeInterestDays} 天免息期
            </span>
          </div>
        )}
        
        {promotionInfo.canInstallment && promotionInfo.monthlyPayment && (
          <div className="benefit-item">
            <span className="benefit-icon">💳</span>
            <span className="benefit-text">
              支持分期，月供仅需 ¥{promotionInfo.monthlyPayment}
            </span>
          </div>
        )}
      </div>
      
      <div className="promotion-description">
        {promotionInfo.description}
      </div>
      
      <div className="promotion-actions">
        <button 
          className="action-btn secondary"
          onClick={() => onAction('learn_more')}
        >
          了解更多
        </button>
        <button 
          className="action-btn primary"
          onClick={() => onAction('use_credit_card')}
        >
          立即使用信用卡
        </button>
      </div>
    </div>
  );
};

// 3. 客服浮动按钮组件
const CustomerServiceFloat = ({ productInfo }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [hasNewMessage, setHasNewMessage] = useState(false);

  // 模拟主动推送消息
  useEffect(() => {
    // 如果用户在商品页面停留超过30秒，显示提示
    const timer = setTimeout(() => {
      if (!isOpen && productInfo?.price > 1000) {
        setHasNewMessage(true);
      }
    }, 30000);

    return () => clearTimeout(timer);
  }, [productInfo, isOpen]);

  const handleOpen = () => {
    setIsOpen(true);
    setHasNewMessage(false);
  };

  const handleClose = () => {
    setIsOpen(false);
  };

  return (
    <>
      {/* 浮动按钮 */}
      {!isOpen && (
        <div className="customer-service-float">
          <button 
            className={`float-btn ${hasNewMessage ? 'has-message' : ''}`}
            onClick={handleOpen}
            title="联系客服"
          >
            <span className="float-icon">💬</span>
            {hasNewMessage && (
              <div className="message-badge">
                <span className="badge-dot"></span>
                <div className="message-popup">
                  💡 客服提示：大额商品建议使用信用卡分期，减轻支付压力！
                </div>
              </div>
            )}
          </button>
        </div>
      )}
      
      {/* 聊天窗口 */}
      <CustomerServiceChat
        isOpen={isOpen}
        onClose={handleClose}
        productInfo={productInfo}
      />
    </>
  );
};

// 4. 支付页面集成组件
const PaymentPageIntegration = ({ orderInfo, onPaymentMethodChange }) => {
  const [showCustomerService, setShowCustomerService] = useState(false);
  const [selectedPayment, setSelectedPayment] = useState('');

  const paymentMethods = [
    {
      id: 'credit_card',
      name: '招商银行信用卡',
      icon: '💳',
      benefits: ['获得积分', '免息分期', '购物保险'],
      recommended: true
    },
    {
      id: 'alipay',
      name: '支付宝',
      icon: '🟦',
      benefits: ['快速支付'],
      recommended: false
    },
    {
      id: 'wechat',
      name: '微信支付',
      icon: '🟢',
      benefits: ['便捷支付'],
      recommended: false
    }
  ];

  const handlePaymentSelect = (paymentId) => {
    setSelectedPayment(paymentId);
    onPaymentMethodChange?.(paymentId);
    
    // 如果选择非信用卡支付，弹出客服推荐
    if (paymentId !== 'credit_card' && orderInfo?.amount > 500) {
      setTimeout(() => {
        setShowCustomerService(true);
      }, 1000);
    }
  };

  return (
    <div className="payment-integration">
      <div className="payment-methods">
        <h3>选择支付方式</h3>
        
        {paymentMethods.map(method => (
          <div 
            key={method.id}
            className={`payment-method ${selectedPayment === method.id ? 'selected' : ''} ${method.recommended ? 'recommended' : ''}`}
            onClick={() => handlePaymentSelect(method.id)}
          >
            <div className="method-header">
              <span className="method-icon">{method.icon}</span>
              <span className="method-name">{method.name}</span>
              {method.recommended && <span className="recommended-badge">推荐</span>}
            </div>
            
            <div className="method-benefits">
              {method.benefits.map((benefit, idx) => (
                <span key={idx} className="benefit-tag">{benefit}</span>
              ))}
            </div>
            
            {method.id === 'credit_card' && orderInfo && (
              <div className="credit-card-preview">
                <div className="preview-item">
                  🎁 可获得 {Math.floor(orderInfo.amount * 0.01)} 积分
                </div>
                <div className="preview-item">
                  ⏰ 享受最长48天免息期
                </div>
                {orderInfo.amount > 500 && (
                  <div className="preview-item">
                    💳 支持12期免息分期
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
      </div>
      
      {/* 客服咨询按钮 */}
      <div className="payment-help">
        <button 
          className="help-btn"
          onClick={() => setShowCustomerService(true)}
        >
          💬 支付遇到问题？咨询客服
        </button>
      </div>
      
      {/* 集成客服组件 */}
      <CustomerServiceChat
        isOpen={showCustomerService}
        onClose={() => setShowCustomerService(false)}
        productInfo={{
          id: orderInfo?.productId,
          name: orderInfo?.productName,
          price: orderInfo?.amount
        }}
      />
    </div>
  );
};

// 5. 主应用集成示例
const App = () => {
  const [currentProduct, setCurrentProduct] = useState(null);

  // 模拟商品数据
  useEffect(() => {
    // 从URL或状态管理获取当前商品信息
    const productInfo = {
      id: 12345,
      name: 'iPhone 15 Pro Max',
      price: 9999
    };
    setCurrentProduct(productInfo);
  }, []);

  return (
    <div className="app">
      {/* 你的现有页面内容 */}
      <div className="page-content">
        {/* 商品详情页、购物车、支付页面等 */}
      </div>
      
      {/* 客服浮动按钮（全局） */}
      <CustomerServiceFloat productInfo={currentProduct} />
    </div>
  );
};

export default App;
export { CustomerServiceChat, CustomerServiceFloat, PaymentPageIntegration };