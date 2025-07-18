/* ==================== 客服聊天窗口样式 ==================== */

/* 主容器 */
.customer-service-chat {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 380px;
  height: 600px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  z-index: 9999;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  overflow: hidden;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 聊天头部 */
.chat-header {
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  color: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 12px 12px 0 0;
}

.header-info h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.online-status {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 2px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cache-indicator {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  cursor: help;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
  scrollbar-width: thin;
  scrollbar-color: #ddd transparent;
}

.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

/* 消息样式 */
.message {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}

.message.user {
  align-items: flex-end;
}

.message.bot {
  align-items: flex-start;
}

.message-content {
  max-width: 85%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.message.user .message-text {
  background: #007bff;
  color: white;
  border-bottom-right-radius: 6px;
}

.message.bot .message-text {
  background: white;
  color: #333;
  border: 1px solid #e9ecef;
  border-bottom-left-radius: 6px;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
  font-size: 11px;
  color: #6c757d;
}

.message.user .message-meta {
  justify-content: flex-end;
}

.response-time {
  font-size: 10px;
  color: #28a745;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.cache-hit {
  color: #17a2b8;
  font-weight: 500;
}

/* 推广卡片样式 */
.promotion-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 16px;
  margin-top: 8px;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.promotion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.promotion-title {
  font-weight: 600;
  font-size: 14px;
}

.promotion-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
}

.promotion-benefits {
  margin: 12px 0;
}

.benefit-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 13px;
}

.benefit-icon {
  font-size: 14px;
}

.benefit-text small {
  opacity: 0.8;
  margin-left: 4px;
}

.promotion-description {
  font-size: 12px;
  opacity: 0.9;
  margin: 8px 0 12px 0;
}

.promotion-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  padding: 8px 12px;
  border-radius: 6px;
  border: none;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.primary {
  background: white;
  color: #667eea;
}

.action-btn.secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 建议回复 */
.suggestions {
  margin-top: 8px;
}

.suggestions-title {
  font-size: 11px;
  color: #6c757d;
  margin-bottom: 6px;
}

.suggestion-btn {
  display: inline-block;
  background: #e9ecef;
  border: none;
  padding: 6px 12px;
  margin: 2px 4px 2px 0;
  border-radius: 12px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.suggestion-btn:hover {
  background: #007bff;
  color: white;
  transform: translateY(-1px);
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 18px;
  border-bottom-left-radius: 6px;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background: #007bff;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.typing-text {
  font-size: 11px;
  color: #6c757d;
  margin-top: 4px;
}

/* 快捷回复区域 */
.quick-responses {
  background: white;
  padding: 12px 16px;
  border-top: 1px solid #e9ecef;
}

.quick-responses-title {
  font-size: 12px;
  color: #6c757d;
  margin-bottom: 8px;
  font-weight: 500;
}

.quick-responses-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}

.quick-response-btn {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  padding: 8px 10px;
  border-radius: 8px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.quick-response-btn:hover:not(:disabled) {
  background: #e9ecef;
  border-color: #007bff;
  transform: translateY(-1px);
}

.quick-response-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 输入区域 */
.chat-input {
  background: white;
  padding: 16px;
  border-top: 1px solid #e9ecef;
}

.input-wrapper {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.input-wrapper textarea {
  flex: 1;
  border: 1px solid #e9ecef;
  border-radius: 20px;
  padding: 10px 16px;
  font-size: 14px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.2s;
  line-height: 1.4;
}

.input-wrapper textarea:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.send-btn {
  background: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s;
}

.send-btn:hover:not(:disabled) {
  background: #0056b3;
  transform: scale(1.05);
}

.send-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.input-hint {
  margin-top: 6px;
  font-size: 10px;
  color: #6c757d;
}

/* ==================== 浮动按钮样式 ==================== */

.customer-service-float {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9998;
}

.float-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(238, 90, 36, 0.4);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-5px);
  }
  60% {
    transform: translateY(-3px);
  }
}

.float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(238, 90, 36, 0.6);
}

.float-btn.has-message {
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 4px 20px rgba(238, 90, 36, 0.4);
  }
  50% {
    box-shadow: 0 4px 20px rgba(238, 90, 36, 0.8);
  }
  100% {
    box-shadow: 0 4px 20px rgba(238, 90, 36, 0.4);
  }
}

.message-badge {
  position: absolute;
  top: -5px;
  right: -5px;
}

.badge-dot {
  width: 12px;
  height: 12px;
  background: #28a745;
  border-radius: 50%;
  display: block;
  position: relative;
  animation: ping 1s infinite;
}

@keyframes ping {
  75%, 100% {
    transform: scale(2);
    opacity: 0;
  }
}

.message-popup {
  position: absolute;
  bottom: 70px;
  right: 0;
  background: white;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  font-size: 12px;
  color: #333;
  white-space: nowrap;
  max-width: 200px;
  white-space: normal;
  animation: fadeInUp 0.3s ease-out;
}

.message-popup::after {
  content: '';
  position: absolute;
  top: 100%;
  right: 20px;
  border: 6px solid transparent;
  border-top-color: white;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ==================== 支付页面集成样式 ==================== */

.payment-integration {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.payment-methods h3 {
  margin-bottom: 16px;
  color: #333;
  font-size: 18px;
}

.payment-method {
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.payment-method:hover {
  border-color: #007bff;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.1);
}

.payment-method.selected {
  border-color: #007bff;
  background: #f8f9ff;
}

.payment-method.recommended {
  border-color: #28a745;
  background: linear-gradient(135deg, #f8fff9, #f0fff4);
}

.payment-method.recommended::before {
  content: '推荐';
  position: absolute;
  top: -8px;
  left: 16px;
  background: #28a745;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
}

.method-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.method-icon {
  font-size: 20px;
}

.method-name {
  font-weight: 500;
  font-size: 16px;
}

.recommended-badge {
  background: #28a745;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
}

.method-benefits {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.benefit-tag {
  background: #e9ecef;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  color: #6c757d;
}

.credit-card-preview {
  background: rgba(0, 123, 255, 0.05);
  border-radius: 8px;
  padding: 12px;
  margin-top: 8px;
}

.preview-item {
  font-size: 12px;
  color: #007bff;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.preview-item:last-child {
  margin-bottom: 0;
}

.payment-help {
  text-align: center;
  margin-top: 20px;
}

.help-btn {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.help-btn:hover {
  background: #e9ecef;
  border-color: #007bff;
}

/* ==================== 响应式设计 ==================== */

@media (max-width: 480px) {
  .customer-service-chat {
    width: calc(100vw - 20px);
    height: calc(100vh - 40px);
    bottom: 10px;
    right: 10px;
    left: 10px;
  }
  
  .quick-responses-grid {
    grid-template-columns: 1fr;
  }
  
  .payment-integration {
    padding: 10px;
  }
  
  .promotion-actions {
    flex-direction: column;
  }
  
  .method-benefits {
    justify-content: flex-start;
  }
}

/* ==================== 动画和过渡效果 ==================== */

.message-enter {
  opacity: 0;
  transform: translateY(20px);
}

.message-enter-active {
  opacity: 1;
  transform: translateY(0);
  transition: all 0.3s ease-out;
}

.suggestion-btn-enter {
  opacity: 0;
  transform: scale(0.8);
}

.suggestion-btn-enter-active {
  opacity: 1;
  transform: scale(1);
  transition: all 0.2s ease-out;
}

/* ==================== 深色模式支持 ==================== */

@media (prefers-color-scheme: dark) {
  .customer-service-chat {
    background: #2c3e50;
    color: #ecf0f1;
  }
  
  .chat-messages {
    background: #34495e;
  }
  
  .message.bot .message-text {
    background: #3498db;
    color: white;
    border-color: #2980b9;
  }
  
  .quick-response-btn {
    background: #34495e;
    border-color: #2c3e50;
    color: #ecf0f1;
  }
  
  .quick-response-btn:hover {
    background: #3498db;
  }
  
  .input-wrapper textarea {
    background: #34495e;
    border-color: #2c3e50;
    color: #ecf0f1;
  }
}